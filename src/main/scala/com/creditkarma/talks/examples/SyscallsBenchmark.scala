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

import java.io.File

import scala.annotation.switch

import org.openjdk.jmh.annotations.{ Benchmark, State, Scope, Setup, Level }

object SyscallsBenchmark {

  val random = new util.Random(System.currentTimeMillis())

  @State(Scope.Benchmark)
  class FileSetup {
    val file: File = new File("bench.txt")

    @Setup(Level.Invocation)
    def initFile(): Unit = {
      if (!file.exists()) file.createNewFile()
      file.setReadable(false)
    }
  }
}

class SyscallsBenchmark {

  import SyscallsBenchmark._

  @Benchmark
  def testMilliClockAccess(): Long = {
    System.currentTimeMillis()
  }

  @Benchmark
  def testNanoClockAccess(): Long = {
    System.nanoTime()
  }

  @Benchmark
  def testChmod(f: FileSetup): Boolean = {
    f.file.setReadable(true)
  }

  @Benchmark
  def testFileDelete(f: FileSetup): Boolean = {
    f.file.delete()
  }

  @Benchmark
  def testRandom(): Long = {
    random.nextLong()
  }
}
