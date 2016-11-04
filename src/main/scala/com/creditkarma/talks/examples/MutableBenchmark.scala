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

object MutableBenchmark {

  val random = new util.Random(System.currentTimeMillis())  

  @State(Scope.Benchmark)
  class MutableValue {

    @Param(Array("16", "256", "4096"))
    var containerSize: Int = _

    val data: scala.collection.mutable.Map[Long, Long] =
      scala.collection.mutable.Map[Long, Long]()
    var key: Long = 0l
    var value: Long = 0l

    @Setup(Level.Iteration)
    def initAll: Unit = {

      for (i <- 0 until containerSize) 
        data.put(random.nextLong(), random.nextLong())
    }

    @Setup(Level.Invocation)
    def init: Unit = {

      key = random.nextLong()
      value = random.nextLong()
    }

    @Setup(Level.Iteration)
    def fin: Unit = {
      data.clear()
    }
  }

  @State(Scope.Benchmark)
  class ImmutableValue {

    @Param(Array("16", "256", "4096"))
    var containerSize: Int = _

    var data: scala.collection.immutable.Map[Long, Long] =
      scala.collection.immutable.Map[Long, Long]()
    var key: Long = 0l
    var value: Long = 0l

    @Setup(Level.Iteration)
    def initAll: Unit = {
      for (i <- 0 until containerSize) 
        data = data + (key -> value)
    }

    @Setup(Level.Invocation)
    def setValue: Unit = {
      key = random.nextLong()
      value = random.nextLong()
    }

    @Setup(Level.Iteration)
    def fin: Unit = {
      data = scala.collection.immutable.Map[Long, Long]()
    }
  }
}

class MutableBenchmark {

  import MutableBenchmark._

  @Benchmark
  def insertMutable(input: MutableValue): Unit = {
    input.data.put(input.key, input.value)
  }

  @Benchmark
  def insertImmutable(input: ImmutableValue): Unit = {
    input.data = input.data + (input.key -> input.value)
  }

  @Benchmark
  def removeMutable(input: MutableValue): Unit = {
    input.data.remove(input.key)
  }

  @Benchmark
  def removeImmutable(input: ImmutableValue): Unit = {
    input.data = input.data - input.key
  }
}
