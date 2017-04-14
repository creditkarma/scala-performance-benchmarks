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

import scala.reflect.ClassTag
import scala.collection.immutable._

import org.openjdk.jmh.annotations.{
  Benchmark, State, Scope, Setup, Level, Param
}

import org.openjdk.jmh.infra.BenchmarkParams

object DataStructureBenchmark {

  val random = new util.Random(System.currentTimeMillis())

  abstract class RandomArray[T](randF: () => T) {

    var arr: Array[T] = _
    var item: T = _

    def getSize: Int

    def setValue()(implicit m: ClassTag[T]): Unit = {
      val size: Int = getSize
      arr = new Array[T](size)
      for (i <- (0 until size)) arr(i) = randF()
      item = arr(random.nextInt(size))
    }
  }

  @State(Scope.Benchmark)
  class RandomIntArray extends RandomArray[Int](random.nextInt) {

    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }

  @State(Scope.Benchmark)
  class RandomLongArray extends RandomArray[Long](random.nextLong) {
    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }

  @State(Scope.Benchmark)
  class RandomDoubleArray extends RandomArray[Double](random.nextDouble) {
    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }

  abstract class RandomSeq[T](randF: () => T) {

    var seq: Seq[T] = _
    var item: T = _

    def getSize: Int

    def setValue(): Unit = {
      val size: Int = getSize
      seq = Seq[T]()
      for (i <- (0 until size)) seq = seq :+ randF()
      item = seq(random.nextInt(size))
    }
  }

  @State(Scope.Benchmark)
  class RandomIntSeq extends RandomSeq[Int](random.nextInt) {
    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }

  @State(Scope.Benchmark)
  class RandomLongSeq extends RandomSeq[Long](random.nextLong) {
    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }

  @State(Scope.Benchmark)
  class RandomDoubleSeq extends RandomSeq[Double](random.nextDouble) {
    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }

  abstract class RandomIndexedSeq[T](randF: () => T) {

    var seq: IndexedSeq[T] = _
    var item: T = _

    def getSize: Int

    def setValue(): Unit = {
      val size: Int = getSize
      seq = IndexedSeq[T]()
      for (i <- (0 until size)) seq = seq :+ randF()
      item = seq(random.nextInt(size))
    }
  }

  @State(Scope.Benchmark)
  class RandomIntIndexedSeq extends RandomIndexedSeq[Int](random.nextInt) {
    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }

  @State(Scope.Benchmark)
  class RandomLongIndexedSeq extends RandomIndexedSeq[Long](random.nextLong) {
    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }

  @State(Scope.Benchmark)
  class RandomDoubleIndexedSeq
      extends RandomIndexedSeq[Double](random.nextDouble) {
    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }

  abstract class RandomMap[T](randF: () => T) {

    var map: Map[T, T] = _
    var item: T = _

    def getSize: Int

    def setValue(): Unit = {
      val size: Int = getSize
      val randomItem: Int = random.nextInt(size)
      map = Map[T, T]()
      for (i <- (0 until size)) {
        val newItem: T = randF()
        map = map + ((newItem, randF()))
        if (i == randomItem) item = newItem
      }
    }
  }

  @State(Scope.Benchmark)
  class RandomIntMap extends RandomMap[Int](random.nextInt) {
    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }

  @State(Scope.Benchmark)
  class RandomLongMap extends RandomMap[Long](random.nextLong) {
    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }

  @State(Scope.Benchmark)
  class RandomDoubleMap extends RandomMap[Double](random.nextDouble) {
    @Param(Array("4", "16", "64", "128", "256", "1024", "4096", "16384", "65536"))
    var size: Int = _

    override def getSize(): Int = size

    @Setup(Level.Invocation)
    def setVal(): Unit = setValue()
  }
}

class DataStructureBenchmark {

  import DataStructureBenchmark._
  
  type RandF[T] = () => T

  // Array cases
  def findItemInArrayWithWhile[T](input: RandomArray[T]): Int = {
    var i = 0
    while (i < input.arr.size) {
      if (input.arr(i) == input.item) return i
      i = i + 1
    }
    -1
  }

  def findItemInArrayWithFind[T](input: RandomArray[T]): Int =
    input.arr.zipWithIndex.find { case(v, idx) => v == input.item } match {
      case Some((_, idx)) => idx
      case None => -1
    }

  @Benchmark
  def findItemInArrayWithWhileInt(input: RandomIntArray): Int =
    findItemInArrayWithWhile[Int](input)

  @Benchmark
  def findItemInArrayWithWhileLong(input: RandomLongArray): Int =
    findItemInArrayWithWhile[Long](input)

  @Benchmark
  def findItemInArrayWithWhileDouble(input: RandomDoubleArray): Int =
    findItemInArrayWithWhile[Double](input)

  @Benchmark
  def findItemInArrayWithFindInt(input: RandomIntArray): Int =
    findItemInArrayWithFind[Int](input)

  @Benchmark
  def findItemInArrayWithFindLong(input: RandomLongArray): Int =
    findItemInArrayWithFind[Long](input)

  @Benchmark
  def findItemInArrayWithFindDouble(input: RandomDoubleArray): Int =
    findItemInArrayWithFind[Double](input)


  // Seq cases
  def findItemInSeqWithWhile[T](input: RandomSeq[T]): Int = {
    var i = 0
    while (i < input.seq.size) {
      if (input.seq(i) == input.item) return i
      i = i + 1
    }
    -1
  }

  def findItemInSeqWithFind[T](input: RandomSeq[T]): Int = 
    input.seq.zipWithIndex.find { case(v, idx) => v == input.item } match {
      case Some((_, idx)) => idx
      case None => -1
    }

  @Benchmark
  def findItemInSeqWithWhileInt(input: RandomIntSeq): Int =
    findItemInSeqWithWhile[Int](input)

  @Benchmark
  def findItemInSeqWithWhileLong(input: RandomLongSeq): Int =
    findItemInSeqWithWhile[Long](input)

  @Benchmark
  def findItemInSeqWithWhileDouble(input: RandomDoubleSeq): Int =
    findItemInSeqWithWhile[Double](input)

  @Benchmark
  def findItemInSeqWithFindInt(input: RandomIntSeq): Int =
    findItemInSeqWithFind[Int](input)

  @Benchmark
  def findItemInSeqWithFindLong(input: RandomLongSeq): Int =
    findItemInSeqWithFind[Long](input)

  @Benchmark
  def findItemInSeqWithFindDouble(input: RandomDoubleSeq): Int =
    findItemInSeqWithFind[Double](input)

  // IndexedSeq cases
  def findItemInIndexedSeqWithWhile[T](input: RandomIndexedSeq[T]): Int = {
    var i = 0
    while (i < input.seq.size) {
      if (input.seq(i) == input.item) return i
      i = i + 1
    }
    -1
  }

  def findItemInIndexedSeqWithFind[T](input: RandomIndexedSeq[T]): Int = 
    input.seq.zipWithIndex.find { case(v, idx) => v == input.item } match {
      case Some((_, idx)) => idx
      case None => -1
    }

  @Benchmark
  def findItemInIndexedSeqWithWhileInt(input: RandomIntIndexedSeq): Int =
    findItemInIndexedSeqWithWhile[Int](input)

  @Benchmark
  def findItemInIndexedSeqWithWhileLong(input: RandomLongIndexedSeq): Int =
    findItemInIndexedSeqWithWhile[Long](input)

  @Benchmark
  def findItemInIndexedSeqWithWhileDouble(input: RandomDoubleIndexedSeq): Int =
    findItemInIndexedSeqWithWhile[Double](input)

  @Benchmark
  def findItemInIndexedSeqWithFindInt(input: RandomIntIndexedSeq): Int =
    findItemInIndexedSeqWithFind[Int](input)

  @Benchmark
  def findItemInIndexedSeqWithFindLong(input: RandomLongIndexedSeq): Int =
    findItemInIndexedSeqWithFind[Long](input)

  @Benchmark
  def findItemInIndexedSeqWithFindDouble(input: RandomDoubleIndexedSeq): Int =
    findItemInIndexedSeqWithFind[Double](input)

  // Map cases
  def findItemInMap[T](input: RandomMap[T]): T =
    input.map.get(input.item) match {
      case Some(t) => t
      case None => -1.asInstanceOf[T]
    }

  @Benchmark
  def findItemInMapInt(input: RandomIntMap): Int = findItemInMap[Int](input)

  @Benchmark
  def findItemInMapLong(input: RandomLongMap): Long = findItemInMap[Long](input)

  @Benchmark
  def findItemInMapDouble(input: RandomDoubleMap): Double =
    findItemInMap[Double](input)
}

