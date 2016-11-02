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

import java.util.concurrent.Callable
import java.util.concurrent.{ Executors, ExecutorService, TimeUnit }

import scala.concurrent.duration.Duration
import scala.util.{ Success, Failure }
import scala.annotation.switch
import scala.collection.immutable.Map
import scala.concurrent.{ ExecutionContext, Future, Await }
import scala.collection.concurrent.TrieMap

import org.openjdk.jmh.annotations.{
  Benchmark, State, Scope, Setup, Level, TearDown, Param
}
import org.openjdk.jmh.infra.Blackhole

object CopyingBenchmark {

  val random = new util.Random(System.currentTimeMillis())

  case class Small(arg1: Long, arg2: Long, arg3: Long, arg4: Long)
  case class SmallStrings(arg1: String, arg2: String)

  case class Medium(
    arg1: Long, arg2: Long, arg3: Long, arg4: Long,
    arg5: Long, arg6: Long, arg7: Long, arg8: Long)
  case class MediumStrings(
    arg1: String, arg2: String, arg3: String, arg4: String,
    arg5: String, arg6: String, arg7: String, arg8: String)

  case class Large(
    arg1: Long, arg2: Long, arg3: Long, arg4: Long,
    arg5: Long, arg6: Long, arg7: Long, arg8: Long,
    arg9: Long, arg10: Long, arg11: Long, arg12: Long,
    arg13: Long, arg14: Long, arg15: Long, arg16: Long)
  case class LargeStrings(
    arg1: String, arg2: String, arg3: String, arg4: String,
    arg5: String, arg6: String, arg7: String, arg8: String,
    arg9: String, arg10: String, arg11: String, arg12: String,
    arg13: String, arg14: String, arg15: String, arg16: String)

  @State(Scope.Benchmark)
  class CopyingSetup {

    def makeSmall(args: Array[Long]): Small =
      Small(args(0), args(1), args(2), args(3))

    def makeSmallStrings(args: Array[String]): SmallStrings =
      SmallStrings(args(0), args(1))

    def makeMedium(args: Array[Long]): Medium =
      Medium(
        args(0), args(1), args(2), args(3), args(4),
        args(5), args(6), args(7))

    def makeMediumStrings(args: Array[String]): MediumStrings =
      MediumStrings(
        args(0), args(1), args(2), args(3), args(4),
        args(5), args(6), args(7))

    def makeLarge(args: Array[Long]): Large =
      Large(
        args(0), args(1), args(2), args(3), args(4),
        args(5), args(6), args(7), args(8), args(9),
        args(10), args(11), args(12), args(13), args(14), args(15))

    def makeLargeStrings(args: Array[String]): LargeStrings =
      LargeStrings(
        args(0), args(1), args(2), args(3), args(4),
        args(5), args(6), args(7), args(8), args(9),
        args(10), args(11), args(12), args(13), args(14), args(15))


    val listOfLongs: Array[Long] = new Array[Long](4096)
    val listOfStrings: Array[String] = new Array[String](4096)

    @Setup(Level.Invocation)
    def init: Unit = {

      for (i <- 0 until 4096) {
        listOfLongs(i) = random.nextLong()
        listOfStrings(i) = random.nextString(random.nextInt(16))
      }
    }
  }
}

class CopyingBenchmark {

  import CopyingBenchmark._

  @Benchmark
  def testCopyingSmallLong(setup: CopyingSetup, bh: Blackhole): Unit = {

    val idx = random.nextInt(4096 - 4)
    bh.consume(setup.makeSmall(setup.listOfLongs.slice(idx, idx + 4)))
  }

  @Benchmark
  def testCopyingSmallString(setup: CopyingSetup, bh: Blackhole): Unit = {

    val idx = random.nextInt(4096 - 2)
    bh.consume(setup.makeSmallStrings(setup.listOfStrings.slice(idx, idx + 2)))
  }

  @Benchmark
  def testCopyingMediumLong(setup: CopyingSetup, bh: Blackhole): Unit = {

    val idx = random.nextInt(4096 - 8)
    bh.consume(setup.makeMedium(setup.listOfLongs.slice(idx, idx + 8)))
  }

  @Benchmark
  def testCopyingMediumString(setup: CopyingSetup, bh: Blackhole): Unit = {

    val idx = random.nextInt(4096 - 8)
    bh.consume(setup.makeMediumStrings(setup.listOfStrings.slice(idx, idx + 8)))
  }

  @Benchmark
  def testCopyingLargeLong(setup: CopyingSetup, bh: Blackhole): Unit = {

    val idx = random.nextInt(4096 - 16)
    bh.consume(setup.makeLarge(setup.listOfLongs.slice(idx, idx + 16)))
  }

  @Benchmark
  def testCopyingLargeString(setup: CopyingSetup, bh: Blackhole): Unit = {

    val idx = random.nextInt(4096 - 16)
    bh.consume(setup.makeLargeStrings(setup.listOfStrings.slice(idx, idx + 16)))
  }
}
