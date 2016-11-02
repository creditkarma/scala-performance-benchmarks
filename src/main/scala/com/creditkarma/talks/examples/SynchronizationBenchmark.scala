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

import java.util.concurrent.atomic._

import org.openjdk.jmh.annotations.{ Benchmark, State, Scope }

object SynchronizationBenchmark {

  @State(Scope.Benchmark)
  class NaiveLongBenchmarkState {
    var counter: Long = 0
  }

  @State(Scope.Benchmark)
  class VolatileLongBenchmarkState {
    @volatile var counter: Long = 0
  }

  @State(Scope.Benchmark)
  class AtomicLongBenchmarkState {
    val counter: AtomicLong = new AtomicLong(0)
  }

  @State(Scope.Benchmark)
  class NaiveIntBenchmarkState {
    var counter: Int = 0
  }

  @State(Scope.Benchmark)
  class VolatileIntBenchmarkState {
    @volatile var counter: Int = 0
  }

  @State(Scope.Benchmark)
  class AtomicIntBenchmarkState {
    val counter: AtomicInteger = new AtomicInteger(0)
  }
}

class SynchronizationBenchmark {

  import SynchronizationBenchmark._

  @Benchmark
  def testNaiveLong(state: NaiveLongBenchmarkState): Long = {
    state.counter = state.counter + 1
    state.counter
  }

  @Benchmark
  def testVolatileLong(state: VolatileLongBenchmarkState): Long = {
    state.counter = state.counter + 1
    state.counter
  }

  @Benchmark
  def testAtomicLong(state: AtomicLongBenchmarkState): Long = {
    state.counter.addAndGet(1)
  }

  @Benchmark
  def testNaiveInt(state: NaiveIntBenchmarkState): Int = {
    state.counter = state.counter + 1
    state.counter
  }

  @Benchmark
  def testVolatileInt(state: VolatileIntBenchmarkState): Int = {
    state.counter = state.counter + 1
    state.counter
  }

  @Benchmark
  def testAtomicInt(state: AtomicIntBenchmarkState): Int = {
    state.counter.addAndGet(1)
  }
}
