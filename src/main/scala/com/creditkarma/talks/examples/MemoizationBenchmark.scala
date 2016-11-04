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

import org.openjdk.jmh.annotations.{ Benchmark, State, Scope, Setup, Param }

@State(Scope.Benchmark)
class MemoizationBenchmark {

  @Param(Array("10", "100", "1000", "10000", "100000", "1000000"))
  var start: Int = _

  @Benchmark
  def computePrimes(): Seq[Int] =
    computePrimesHelper(start)

  def computePrimesHelper(n: Int): Seq[Int] =
    for (i <- 2 to n; if (isPrime(i))) yield i

  def isPrime(check: Int): Boolean = {

    for (i <- 2 to Math.sqrt(check.toDouble).toInt) {
      val mult: Int = check / i
      if (mult * i == check) return false
    }
    true
  }

  // Sieve of Eratosthenes
  @Benchmark
  def withCacheComputePrimes(): Seq[Int] =
    computePrimesWithCacheHelper(start)

  def computePrimesWithCacheHelper(n: Int): Seq[Int] = {

    val primeState: Array[Boolean] = new Array[Boolean](n)
    for (i <- 0 until n) primeState(i) = true

    for (i <- 2 to Math.sqrt(n.toDouble).toInt) {
      if (primeState(i)) {
        for (j <- (i * i) until n by i) {
          primeState(j) = false
        }
      }
    }

    primeState.zipWithIndex.filter { case(v, idx) => v }.map {
      case (_, idx) => idx }
  }
}
