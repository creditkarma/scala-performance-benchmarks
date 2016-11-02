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

import scala.annotation.tailrec
import scala.math.Numeric

import org.openjdk.jmh.annotations.{
  Benchmark, State, Scope, Setup, Level, Param
}


object RecursionBenchmark {

  val random = new util.Random(System.currentTimeMillis())
}

@State(Scope.Benchmark)
class RecursionBenchmark {

  import RecursionBenchmark._

  @Param(Array("10", "100", "1000"))
  var start: Int = _

  @Benchmark
  def calculateByRecursionBenchInt(): Int =
    calculateByRecursion[Int](start)

  @Benchmark
  def calculateByRecursionBenchLong(): Long =
    calculateByRecursion[Long](start)

  @Benchmark
  def calculateByRecursionBenchBigInt(): BigInt =
    calculateByRecursion[BigInt](start)

  @Benchmark
  def calculateByRecursionBenchDouble(): Double =
    calculateByRecursion[Double](start)

  def calculateByRecursion[T](n: T)(implicit x: Numeric[T]): T = {
    import x._
    if (1 == n) 1.asInstanceOf[T]
    else n * calculateByRecursion(n - 1.asInstanceOf[T])
  }

  @Benchmark
  def calculateByLoopBenchInt(): Int = calculateByLoop[Int](start)

  @Benchmark
  def calculateByLoopBenchLong(): Long = calculateByLoop[Long](start)

  @Benchmark
  def calculateByLoopBenchBigInt(): BigInt = calculateByLoop[BigInt](start)

  @Benchmark
  def calculateByLoopBenchDouble(): Double = calculateByLoop[Double](start)

  def calculateByLoop[T](n: T)(implicit x: Numeric[T]): T = {
    import x._
    var res: T = 1.asInstanceOf[T]
    var i: Int = 2
    while (i <= n.asInstanceOf[Int]) {
      res *= i.asInstanceOf[T]
      i = i + 1
    }
    res
  }

  @Benchmark
  def calculateByForComprehensionBenchInt(): Int =
    calculateByForComprehension[Int](start)

  @Benchmark
  def calculateByForComprehensionBenchLong(): Long =
    calculateByForComprehension[Long](start)

  @Benchmark
  def calculateByForComprehensionBenchBigInt(): BigInt =
    calculateByForComprehension[BigInt](start)

  @Benchmark
  def calculateByForComprehensionBenchDouble(): Double =
    calculateByForComprehension[Double](start)

  def calculateByForComprehension[T](n: T)(implicit x: Numeric[T]): T = {
    import x._
    var res: T = 1.asInstanceOf[T]
    for (i <- 2 to n.asInstanceOf[Int])
      res = i.asInstanceOf[T] * res
    res
  }

  @Benchmark
  def calculateByTailRecursionBenchInt(): Int =
    calculateByTailRecursion[Int](start)

  @Benchmark
  def calculateByTailRecursionBenchLong(): Long =
    calculateByTailRecursion[Long](start)

  @Benchmark
  def calculateByTailRecursionBenchBigInt(): BigInt =
    calculateByTailRecursion[BigInt](start)

  @Benchmark
  def calculateByTailRecursionBenchDouble(): Double =
    calculateByTailRecursion[Double](start)

  def calculateByTailRecursion[T](n: T)(implicit x: Numeric[T]): T = {
    import x._

    @tailrec def fac(n: T, acc: T): T = {
      if (1.asInstanceOf[T] == n) acc
      else fac(n - 1.asInstanceOf[T], n * acc)
    }

    fac(n, 1.asInstanceOf[T])
  }
}
