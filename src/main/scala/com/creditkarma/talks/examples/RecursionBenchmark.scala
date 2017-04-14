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
import scala.collection.immutable.NumericRange.Inclusive

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
    calculateByRecursionBenchIntHelper(start)

  def calculateByRecursionBenchIntHelper(n: Int): Int = {
    if (1 == n) 1
    else n * calculateByRecursionBenchIntHelper(n - 1)
  }
  
  @Benchmark
  def calculateByRecursionBenchLong(): Long =
    calculateByRecursionBenchLongHelper(start)

  def calculateByRecursionBenchLongHelper(n: Long): Long = {
    if (1l == n) 1l
    else n * calculateByRecursionBenchLongHelper(n - 1l)
  }
  
  @Benchmark
  def calculateByRecursionBenchBigInt(): BigInt =
    calculateByRecursionBenchBigIntHelper(start)

  def calculateByRecursionBenchBigIntHelper(n: BigInt): BigInt = {
    if (BigInt(1) == n) BigInt(1)
    else n * calculateByRecursionBenchBigIntHelper(n - BigInt(1))
  }
  
  @Benchmark
  def calculateByRecursionBenchDouble(): Double =
    calculateByRecursionBenchDoubleHelper(start)

  def calculateByRecursionBenchDoubleHelper(n: Double): Double = {
    if (1.0 == n) 1.0
    else n * calculateByRecursionBenchDoubleHelper(n - 1.0)
  }
  
  @Benchmark
  def calculateByLoopBenchInt(): Int = calculateByLoop[Int](start, 1)

  @Benchmark
  def calculateByLoopBenchLong(): Long = calculateByLoop[Long](start, 1l)

  @Benchmark
  def calculateByLoopBenchBigInt(): BigInt =
    calculateByLoop[BigInt](start, BigInt(1))

  @Benchmark
  def calculateByLoopBenchDouble(): Double = calculateByLoop[Double](start, 1.0)

  def calculateByLoop[T](n: T, oneV: T)(implicit x: Numeric[T]): T = {
    import x._
    var res: T = oneV
    var i: T = oneV + oneV
    while (i <= n) {
      res *= i
      i = i + oneV
    }
    res
  }

  @Benchmark
  def calculateByForComprehensionBenchInt(): Int = {
    var res: Int = 1
    for (i <- 2 to start) res = i * res
    res
  }

  @Benchmark
  def calculateByForComprehensionBenchLong(): Long = {
    var res: Long = 1
    for (i <- 2 to start) res = i.toLong * res
    res
  }

  @Benchmark
  def calculateByForComprehensionBenchBigInt(): BigInt = {
    var res: BigInt = BigInt(1)
    for (i <- 2 to start) res = BigInt(i) * res
    res
  }

  @Benchmark
  def calculateByForComprehensionBenchDouble(): Double = {
    var res: Double = 1.0
    for (i <- 2 to start) res = i.toDouble * res
    res
  }

  @Benchmark
  def calculateByTailRecursionBenchInt(): Int =
    calculateByTailRecursion[Int](start, 1)

  @Benchmark
  def calculateByTailRecursionBenchLong(): Long =
    calculateByTailRecursion[Long](start, 1l)

  @Benchmark
  def calculateByTailRecursionBenchBigInt(): BigInt =
    calculateByTailRecursion[BigInt](start, BigInt(1))

  @Benchmark
  def calculateByTailRecursionBenchDouble(): Double =
    calculateByTailRecursion[Double](start, 1.0)

  def calculateByTailRecursion[T](n: T, oneV: T)(implicit x: Numeric[T]): T = {
    import x._

    @tailrec def fac(n: T, acc: T): T = {
      if (oneV == n) acc
      else fac(n - oneV, n * acc)
    }

    fac(n, oneV)
  }

  @Benchmark
  def calculateByFoldleftBenchInt(): Int =
    calculateByFoldleft[Int](start)

  @Benchmark
  def calculateByFoldleftBenchLong(): Long =
    calculateByFoldleft[Long](start)

  @Benchmark
  def calculateByFoldleftBenchBigInt(): BigInt =
    calculateByFoldleft[BigInt](start)

  @Benchmark
  def calculateByFoldleftBenchDouble(): Double = {
    implicit val num = Numeric.DoubleAsIfIntegral
    calculateByFoldleft[Double](start)
  }

  def calculateByFoldleft[T](n: T)(implicit num: math.Integral[T]): T = {
    import num._
    (new Inclusive[T](plus(one, one), n, one)).foldLeft(one)(_*_)
  }
}
