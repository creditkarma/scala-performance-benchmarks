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
  def calculateByLoopBenchInt(): Int = {
    var res: Int = 1
    var i: Int = 2
    while (i <= start) {
      res *= i
      i = i + 1
    }
    res
  }

  @Benchmark
  def calculateByLoopBenchLong(): Long = {
    var res: Long = 1l
    var i: Long = 2l
    while (i <= start.toLong) {
      res *= i
      i = i + 1l
    }
    res
  }

  @Benchmark
  def calculateByLoopBenchBigInt(): BigInt = {
    var res: BigInt = BigInt(1)
    var i: Int = 2
    while (i <= start) {
      res *= BigInt(i)
      i = i + 1
    }
    res
  }

  @Benchmark
  def calculateByLoopBenchDouble(): Double = {
    var res: Double = 1.0
    var i: Int = 2
    while (i <= start) {
      res *= i.toDouble
      i = i + 1
    }
    res
  }

  @Benchmark
  def calculateByForComprehensionBenchInt(): Int = {
    var res: Int = 1
    for (i <- 2 until start) res = i * res
    res
  }

  @Benchmark
  def calculateByForComprehensionBenchLong(): Long = {
    var res: Long = 1
    for (i <- 2 until start) res = i.toLong * res
    res
  }

  @Benchmark
  def calculateByForComprehensionBenchBigInt(): BigInt = {
    var res: BigInt = BigInt(1)
    for (i <- 2 until start) res = BigInt(i) * res
    res
  }

  @Benchmark
  def calculateByForComprehensionBenchDouble(): Double = {
    var res: Double = 1.0
    for (i <- 2 until start) res = i.toDouble * res
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
      if (one == n) acc
      else fac(n - one, n * acc)
    }

    fac(n, one)
  }

  @Benchmark
  def calculateByFoldleftBenchInt(): Int =
    (2 until start).foldLeft(1)(_*_)

  @Benchmark
  def calculateByFoldleftBenchLong(): Long =
    (2l until start.toLong).foldLeft(1l)(_*_)

  @Benchmark
  def calculateByFoldleftBenchBigInt(): BigInt =
    (2 until start).map { BigInt(_) }.foldLeft(BigInt(1))(_*_)

  @Benchmark
  def calculateByFoldleftBenchDouble(): Double =
    (2 until start).map { _.toDouble }.foldLeft(1.0)(_*_)
}
