/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.creditkarma.talks.examples

import java.io.{
  File, FileInputStream, FileOutputStream, InputStream, IOException
}
import java.nio.ByteBuffer
import java.nio.channels.{ AsynchronousFileChannel, CompletionHandler }
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.WRITE
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.{ Path, Files, OpenOption, StandardOpenOption }
import java.nio.file.attribute.{ PosixFilePermission, PosixFilePermissions }
import java.util.{ Set, HashSet }
import java.util.concurrent.{ Executors, ExecutorService, Future, TimeUnit }
import java.util.concurrent.atomic.AtomicInteger

import scala.annotation.switch

import org.openjdk.jmh.annotations.{
  Benchmark, State, Scope, Setup, Level, Param, TearDown
}

object IOBenchmark {

  val random = new util.Random(System.currentTimeMillis())

  private def fillWithRandomJunk(f: File, numBytes: Int): Unit = {

    val buf: Array[Byte] = new Array[Byte](4096)
    val write: FileOutputStream = new FileOutputStream(f)
    var bytesWritten: Int = 0

    while (bytesWritten < numBytes) {
      val toWrite: Int = Math.min(4096, numBytes - bytesWritten)
      random.nextBytes(buf)
      write.write(buf, 0, toWrite)
      bytesWritten = bytesWritten + toWrite
    }
    write.flush()
    write.close()
  }
  
  // args to help open this file for reading
  val readOpenOptions: Set[OpenOption] = new HashSet[OpenOption]()
  readOpenOptions.add(READ)
  val writeOpenOptions: Set[OpenOption] = new HashSet[OpenOption]()
  writeOpenOptions.add(CREATE)
  writeOpenOptions.add(WRITE)
  val permissions: Set[PosixFilePermission] = new HashSet[PosixFilePermission]()
  permissions.add(PosixFilePermission.OWNER_READ)
  permissions.add(PosixFilePermission.OWNER_WRITE)
  
  @State(Scope.Benchmark)
  class FileReadingSetup {

    val file: File = new File("bench-tests")
    if (!file.exists()) require(file.mkdir())
    require(file.isDirectory())

    @Param(Array("1", "2", "4", "8"))
    var numThreads: Int = _

    // 1KB, 1MB, 1GB
    @Param(Array("1024", "1048576", "1073741824"))
    var fileSize: Int = _

    @Setup(Level.Invocation)
    def initFile(): Unit = {

      for (i <- 1 to numThreads) {

        val toWrite: File = new File(file, s"read${i}.txt")
        toWrite.setReadable(true)
        toWrite.setWritable(true)
        fillWithRandomJunk(toWrite, fileSize)
      }
    }

    @TearDown(Level.Invocation)
    def deleteDir(): Unit = {

      file.listFiles().foreach { _.delete() }
      //file.delete()
    }
  }

  @State(Scope.Benchmark)
  class FileWritingSetup {

    val file: File = new File("bench-tests")
    if (!file.exists()) require(file.mkdir())
    require(file.isDirectory())

    @Param(Array("1", "2", "4", "8"))
    var numThreads: Int = _

    // 1KB, 1MB, 1GB
    @Param(Array("1024", "1048576", "1073741824"))
    var fileSize: Int = _
    
    val junkToWrite: Array[Byte] = new Array[Byte](fileSize)
    random.nextBytes(junkToWrite)
    
    @Setup(Level.Invocation)
    def initFile(): Unit = {
    }

    @TearDown(Level.Invocation)
    def deleteDir(): Unit = {

      file.listFiles().foreach { _.delete() }
      //file.delete()
    }
  }
}

class IOBenchmark {

  import IOBenchmark._

  private def closeThreadpool(threadPool: ExecutorService): Unit = {

    // teardown threadpool
    threadPool.shutdown()
    try {
      if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
        threadPool.shutdownNow()
        if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
          println("pool didn't terminal")
        }
      }
    } catch {
      case ie: InterruptedException =>
        threadPool.shutdownNow()
        Thread.currentThread().interrupt()
    }
  }

  @Benchmark
  def testBlockingWrite(f: FileWritingSetup): Int = {

    var numBytes: AtomicInteger = new AtomicInteger(0)

    class BlockingWrite(threadNum: Int) extends Runnable {
      def run(): Unit = {

        val toWrite: File = new File(f.file, s"write${threadNum}.txt")
        val fos: FileOutputStream = new FileOutputStream(toWrite)

        var pos: Int = 0

        while (pos < f.junkToWrite.length) {

          val numToWrite: Int = Math.min(4096, f.junkToWrite.length - pos)
          fos.write(f.junkToWrite, pos, numToWrite)
          pos = pos + numToWrite
        }

        fos.flush()
        fos.close()
        numBytes.addAndGet(pos)
      }
    }

    val threadPool = Executors.newFixedThreadPool(f.numThreads)
    val futures: Seq[Future[_]] =
      for (i <- 1 to f.numThreads) 
        yield threadPool.submit(new BlockingWrite(i))

    // wait for all future to complete
    futures.foreach(_.get())
    // teardown threadpool
    closeThreadpool(threadPool)

    numBytes.get()
  }

  @Benchmark
  def testNonBlockingWrite(f: FileWritingSetup): Int = {

    var numBytes: AtomicInteger = new AtomicInteger(0)
    val threadPool = Executors.newFixedThreadPool(f.numThreads)

    class NonBlockingWriter(threadNum: Int)
        extends CompletionHandler[Integer, Long] {

      @volatile var done: Boolean = false

      // the file handle we are writing to
      val toWrite: File = new File(f.file, s"write${threadNum}.txt")
      val afc: AsynchronousFileChannel = AsynchronousFileChannel.open(
        toWrite.toPath, writeOpenOptions, threadPool,
        PosixFilePermissions.asFileAttribute(permissions))

      def completed(result: Integer, pos: Long): Unit = {

        if (result > 0) {

          val startPos: Int = pos.toInt + result
          val writeLen: Int = Math.min(4096, f.junkToWrite.length - startPos)
          afc.write(
            ByteBuffer.wrap(f.junkToWrite, startPos, writeLen),
            startPos, startPos.toLong, this)

        } else {

          numBytes.addAndGet((pos + result).toInt)
          done = true
        }
      }

      def failed(exc: Throwable, pos: Long): Unit = {
        done = true
      }

      private val endPos: Int = Math.min(4096, f.junkToWrite.length)
      afc.write(ByteBuffer.wrap(f.junkToWrite, 0, endPos), 0, 0l, this)
    }

    // the file handle we are reading from
    val writers: Seq[NonBlockingWriter] = 
      for (i <- 1 to f.numThreads) yield new NonBlockingWriter(i)

    // wait for all the readers to finish
    writers.foreach { writer => while (!writer.done) Thread.sleep(100) }

    // teardown threadpool
    closeThreadpool(threadPool)
    
    numBytes.get()
  }

  @Benchmark
  def testBlockingRead(f: FileReadingSetup): Int = {

    var numBytes: AtomicInteger = new AtomicInteger(0)
    val threadPool = Executors.newFixedThreadPool(f.numThreads)

    class BlockingRead(threadNum: Int) extends Runnable {
      def run(): Unit = {
        val toRead: File = new File(f.file, s"read${threadNum}.txt")
        val fis: FileInputStream = new FileInputStream(toRead)
        val buf: Array[Byte] = new Array[Byte](4096)
        var numRead: Int = 0
        var read: Int = fis.read(buf)

        while (read != -1) {

          numRead = numRead + read
          read = fis.read(buf)
        }

        numBytes.addAndGet(numRead)
      }
    }

    val futures: Seq[Future[_]] =
      for (i <- 1 to f.numThreads) 
        yield threadPool.submit(new BlockingRead(i))

    // wait for all future to complete
    futures.foreach(_.get())
    // teardown threadpool
    closeThreadpool(threadPool)

    numBytes.get()
  }

  @Benchmark
  def testNonBlockingRead(f: FileReadingSetup): Int = {

    var numBytes: AtomicInteger = new AtomicInteger(0)
    val threadPool = Executors.newFixedThreadPool(f.numThreads)

    class NonBlockingReader(threadNum: Int)
        extends CompletionHandler[Integer, Long] {

      @volatile var done: Boolean = false
      val buf: ByteBuffer = ByteBuffer.allocate(4096)
      val toRead: File = new File(f.file, s"read${threadNum}.txt")
      val afc: AsynchronousFileChannel = AsynchronousFileChannel.open(
        toRead.toPath, readOpenOptions, threadPool,
        PosixFilePermissions.asFileAttribute(permissions))

      def completed(result: Integer, pos: Long): Unit = {

        if (result > 0) {

          afc.read(buf, pos + result, pos + result, this)

        } else {

          numBytes.addAndGet((pos + result).toInt)
          done = true
        }
      }

      def failed(exc: Throwable, pos: Long): Unit = {
        done = true
      }

      afc.read(buf, 0, 0l, this)
    }

    // the file handle we are reading from
    val readers: Seq[NonBlockingReader] = 
      for (i <- 1 to f.numThreads) yield new NonBlockingReader(i)

    // wait for all the readers to finish
    readers.foreach { reader => while (!reader.done) Thread.sleep(100) }

    // teardown threadpool
    closeThreadpool(threadPool)

    numBytes.get()
  }
}
