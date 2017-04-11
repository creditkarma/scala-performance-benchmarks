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

import scala.annotation.switch

import org.openjdk.jmh.annotations.{
  Benchmark, State, Scope, Setup, Level, Param
}
import org.openjdk.jmh.infra.Blackhole

object MutableBenchmark {

  val random = new util.Random(System.currentTimeMillis())  

  @State(Scope.Benchmark)
  class ConcurrentMapValue {

    @Param(Array("16", "256", "4096"))
    var containerSize: Int = _

    val data: java.util.concurrent.ConcurrentHashMap[Long, Long] =
      new java.util.concurrent.ConcurrentHashMap[Long, Long]()
    var key: Long = 0l
    var value: Long = 0l
    var toRead: Long = 0l

    @Setup(Level.Iteration)
    def initAll: Unit = {

      data.clear()
      for (i <- 0 until containerSize) {
        toRead = random.nextLong
        data.put(toRead, random.nextLong)
      }
    }

    @Setup(Level.Invocation)
    def init: Unit = {

      key = random.nextLong()
      value = random.nextLong()
      val idx = random.nextInt(containerSize)
      val it = data.keySet().iterator
      for (_ <- 1 to idx) it.next()
      toRead = it.next()
    }
  }

  @State(Scope.Benchmark)
  class MutableBufferValue {

    @Param(Array("16", "256", "4096"))
    var containerSize: Int = _

    val data: scala.collection.mutable.Buffer[Long] =
      scala.collection.mutable.Buffer[Long]()
    var value: Long = 0l
    var toRead: Int = 0

    @Setup(Level.Iteration)
    def initAll: Unit = {

      data.clear()
      for (i <- 0 until containerSize) data += random.nextLong()
      toRead = random.nextInt(containerSize)
    }

    @Setup(Level.Invocation)
    def init: Unit = {

      value = random.nextLong()
      toRead = random.nextInt(containerSize)
    }
  }

  @State(Scope.Benchmark)
  class MutableMapValue {

    @Param(Array("16", "256", "4096"))
    var containerSize: Int = _

    val data: scala.collection.mutable.Map[Long, Long] =
      scala.collection.mutable.Map[Long, Long]()
    var key: Long = 0l
    var value: Long = 0l
    var toRead: Long = 0

    @Setup(Level.Iteration)
    def initAll: Unit = {

      data.clear()
      for (i <- 0 until containerSize) 
        data.put(random.nextLong(), random.nextLong())
    }

    @Setup(Level.Invocation)
    def init: Unit = {

      key = random.nextLong()
      value = random.nextLong()
      toRead = data.drop(random.nextInt(containerSize)).head._1
    }
  }

  @State(Scope.Benchmark)
  class ImmutableSeqValue {

    @Param(Array("16", "256", "4096"))
    var containerSize: Int = _

    var data: scala.collection.immutable.Seq[Long] =
      scala.collection.immutable.Seq[Long]()
    var value: Long = 0l
    var toRead: Int = 0

    @Setup(Level.Iteration)
    def initAll: Unit = {

      data = scala.collection.immutable.Seq[Long]()
      for (i <- 0 until containerSize)
        data = data :+ random.nextLong()
      toRead = random.nextInt(containerSize)
    }

    @Setup(Level.Invocation)
    def setValue: Unit = {

      value = random.nextLong()
      toRead = random.nextInt(containerSize)
    }
  }

  @State(Scope.Benchmark)
  class ImmutableMapValue {

    @Param(Array("16", "256", "4096"))
    var containerSize: Int = _

    var data: scala.collection.immutable.Map[Long, Long] =
      scala.collection.immutable.Map[Long, Long]()
    var key: Long = 0l
    var value: Long = 0l
    var toRead: Long = 0l

    @Setup(Level.Iteration)
    def initAll: Unit = {

      data = scala.collection.immutable.Map[Long, Long]()
      for (i <- 0 until containerSize)
        data = data + (random.nextLong() -> random.nextLong())
      toRead = data.drop(random.nextInt(containerSize)).head._1
    }

    @Setup(Level.Invocation)
    def setValue: Unit = {

      key = random.nextLong()
      value = random.nextLong()
      toRead = data.drop(random.nextInt(containerSize)).head._1
    }
  }
}

class MutableBenchmark {

  import MutableBenchmark._

  @Benchmark
  def readMutable(input: MutableBufferValue, bh: Blackhole): Unit =
    bh.consume(input.data(input.toRead))

  @Benchmark
  def readImmutable(input: ImmutableSeqValue, bh: Blackhole): Unit =
    bh.consume(input.data(input.toRead))

  @Benchmark
  def insertMutable(input: MutableBufferValue): Unit = 
    input.data += input.value

  @Benchmark
  def insertImmutable(input: ImmutableSeqValue): Unit =
    input.data = input.data :+ input.value

  @Benchmark
  def removeMutable(input: MutableBufferValue): Unit =
    input.data -= input.value

  @Benchmark
  def removeImmutable(input: ImmutableSeqValue): Unit =
    input.data = input.data.filterNot { _ == input.value }

  @Benchmark
  def readMutableMap(input: MutableMapValue, bh: Blackhole): Unit = 
    bh.consume(input.data(input.toRead))

  @Benchmark
  def readConcurrentMap(input: ConcurrentMapValue, bh: Blackhole): Unit = 
    bh.consume(input.data.get(input.toRead))

  @Benchmark
  def readImmutableMap(input: ImmutableMapValue, bh: Blackhole): Unit =
    bh.consume(input.data(input.toRead))

  @Benchmark
  def insertMutableMap(input: MutableMapValue): Unit = 
    input.data.put(input.key, input.value)

  @Benchmark
  def insertConcurrentMap(input: ConcurrentMapValue): Unit = 
    input.data.put(input.key, input.value)

  @Benchmark
  def insertImmutableMap(input: ImmutableMapValue): Unit =
    input.data = input.data + (input.key -> input.value)

  @Benchmark
  def removeMutableMap(input: MutableMapValue): Unit =
    input.data.remove(input.key)

  @Benchmark
  def removeConcurrentMap(input: ConcurrentMapValue): Unit =
    input.data.remove(input.key)

  @Benchmark
  def removeImmutableMap(input: ImmutableMapValue): Unit =
    input.data = input.data - input.key
}
