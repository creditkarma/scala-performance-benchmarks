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

import org.openjdk.jmh.annotations.{ Benchmark, State, Scope, Setup, Level }

object SwitchBenchmark {

  val random = new util.Random(System.currentTimeMillis())

  trait Value[T] {
    val res: Long = random.nextLong()

    def getResult(): String
    def getValue(): Int
    def getValue2(): Long = res
  }

  class IntValue extends Value[Int] {
    def getResult(): String = "Int"
    def getValue(): Int = 0
  }

  class LongValue extends Value[Long] {
    def getResult(): String = "Long"
    def getValue(): Int = 1
  }

  class FloatValue extends Value[Float] {
    def getResult(): String = "Float"
    def getValue(): Int = 2
  }

  class DoubleValue extends Value[Double] {
    def getResult(): String = "Double"
    def getValue(): Int = 3
  }

  class StringValue extends Value[String] {
    def getResult(): String = "String"
    def getValue(): Int = 4
  }

  class SeqValue extends Value[Seq[_]] {
    def getResult(): String = "Seq"
    def getValue(): Int = 5
  }

  @State(Scope.Benchmark)
  class RandomValue4 {

    var value: Value[_] = computeValue

    def computeValue: Value[_] = random.nextInt(4) match {
      case 3 => new IntValue
      case 2 => new LongValue
      case 1 => new FloatValue
      case 0 => new DoubleValue
      case _ => throw new RuntimeException("???")
    }

    @Setup(Level.Invocation)
    def setValue: Unit = {
      value = computeValue
    }
  }

  @State(Scope.Benchmark)
  class RandomValue5 {

    var value: Value[_] = computeValue

    def computeValue: Value[_] = random.nextInt(5) match {
      case 3 => new IntValue
      case 2 => new LongValue
      case 1 => new FloatValue
      case 0 => new DoubleValue
      case 4 => new StringValue
      case _ => throw new RuntimeException("???")
    }

    @Setup(Level.Invocation)
    def setValue: Unit = {
      value = computeValue
    }
  }

  @State(Scope.Benchmark)
  class RandomValue6 {

    var value: Value[_] = computeValue

    def computeValue: Value[_] = random.nextInt(6) match {
      case 3 => new IntValue
      case 2 => new LongValue
      case 1 => new FloatValue
      case 0 => new DoubleValue
      case 4 => new StringValue
      case 5 => new SeqValue
      case _ => throw new RuntimeException("???")
    }

    @Setup(Level.Invocation)
    def setValue: Unit = {
      value = computeValue
    }
  }
}

class SwitchBenchmark {

  import SwitchBenchmark._

  @Benchmark
  def testMatchOnClass4(input: RandomValue4): String = {
    val v = input.value
    v match {
      case i: IntValue => i.toString()
      case l: LongValue => l.toString()
      case f: FloatValue => f.getResult()
      case d: DoubleValue => d.getResult()
      case _ => throw new RuntimeException("???")
    }
  }

  @Benchmark
  def testMatchOnString4(input: RandomValue4): String = {
    val v = input.value
    v.getResult() match {
      case "Int" => v.toString()
      case "Long" => v.getResult()
      case "Float" => v.toString()
      case "Double" => v.getResult()
      case _ => throw new RuntimeException("???")
    }
  }

  @Benchmark
  def testMatchOnClass6(input: RandomValue6): String = {
    val v = input.value
    v match {
      case i: IntValue => i.toString()
      case l: LongValue => l.toString()
      case f: FloatValue => f.getResult()
      case d: DoubleValue => d.getResult()
      case s: StringValue => s.toString()
      case se: SeqValue => se.getResult()
      case _ => throw new RuntimeException("???")
    }
  }

  @Benchmark
  def testMatchOnString6(input: RandomValue6): String = {
    val v = input.value
    v.getResult() match {
      case "Int" => v.toString()
      case "Long" => v.getResult()
      case "Float" => v.toString()
      case "Double" => v.getResult()
      case "String" => v.toString()
      case "Seq" => v.getResult()
      case _ => throw new RuntimeException("???")
    }
  }

  @Benchmark
  def testMatchOnLong4(input: RandomValue4): String = {
    val v = input.value
    v.getValue() match {
      case 0 => v.getResult()
      case 1 => v.getResult()
      case 2 => v.toString()
      case 3 => v.toString()
      case _ => throw new RuntimeException("???")
    }
  }

  @Benchmark
  def testMatchOnLong5(input: RandomValue5): String = {
    val v = input.value
    v.getValue() match {
      case 0 => v.getResult()
      case 1 => v.getResult()
      case 2 => v.toString()
      case 3 => v.toString()
      case 4 => v.toString()
      case _ => throw new RuntimeException("???")
    }
  }

  @Benchmark
  def testMatchOnLong6(input: RandomValue6): String = {
    val v = input.value
    v.getValue() match {
      case 0 => v.getResult()
      case 1 => v.getResult()
      case 2 => v.toString()
      case 3 => v.toString()
      case 4 => v.toString()
      case 5 => v.getResult()
      case _ => throw new RuntimeException("???")
    }
  }
}
