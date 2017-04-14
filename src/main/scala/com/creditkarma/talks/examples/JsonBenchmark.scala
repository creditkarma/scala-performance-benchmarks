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

import java.io._

import org.openjdk.jmh.annotations.{
  Benchmark, State, Scope, Setup, Level, TearDown, Param
}
import org.openjdk.jmh.infra.Blackhole

import org.json4s._
import org.json4s.jackson.compactJson
import org.json4s.jackson.Json4sScalaModule
import org.json4s.jackson.JsonMethods.parse
import org.json4s.JsonDSL._

import com.fasterxml.jackson.databind.{
  MappingIterator, ObjectReader, ObjectMapper }
import com.fasterxml.jackson.databind.DeserializationFeature._
import com.fasterxml.jackson.core._

object JsonBenchmark {

  val random = new scala.util.Random(System.currentTimeMillis())

  private def generateString(bufLen: Int): String = {

    val buf: Array[Byte] = new Array[Byte](bufLen)
    for (i <- 0 until bufLen) { buf(i) = (random.nextInt(26) + 65).toByte }
    new String(buf)
  }

  // create byte stream of json to parse
  @State(Scope.Benchmark)
  class ParsingSetup {

    var str: String = null

    @Setup(Level.Iteration)
    def initAll: Unit = {

      val value1: String = generateString(random.nextInt(256))
      val value2 = random.nextInt()
      val value3: String = generateString(random.nextInt(256))
      val value4 = random.nextInt()
      val value5: String = generateString(random.nextInt(256))
      val value6 = random.nextInt()
      val value7: String = generateString(random.nextInt(256))
      val value8 = random.nextInt()
      val value9: String = generateString(random.nextInt(256))
      val value10 = random.nextInt()
      val value11: String = generateString(random.nextInt(256))
      val value12 = random.nextInt()
      val value13: String = generateString(random.nextInt(256))
      val value14 = random.nextInt()
      val value15: String = generateString(random.nextInt(256))
      val value16 = random.nextInt()
      val value17: String = generateString(random.nextInt(256))
      val value18 = random.nextInt()
      val value19: String = generateString(random.nextInt(256))
      val value20 = random.nextInt()
      val value21: String = generateString(random.nextInt(256))
      val value22 = random.nextInt()
      val value23: String = generateString(random.nextInt(256))
      val value24 = random.nextInt()

      str = s"""{ "arg1": "${value1}", "arg2": ${value2}, "arg3": "${value3}", "arg4": ${value4}, "arg5": "${value5}", "arg6": ${value6}, "arg7": "${value7}", "arg8": ${value8}, "arg9": "${value9}", "arg10": ${value10}, "arg11": "${value11}", "arg12": ${value12}, "arg13": "${value13}", "arg14": ${value14}, "arg15": "${value15}", "arg16": ${value16}, "arg17": "${value17}", "arg18": ${value18}, "arg19": "${value19}", "arg20": ${value20}, "arg21": "${value21}", "arg22": ${value22}, "arg23": "${value23}", "arg24": ${value24} }"""
    }
  }

  // create json objects to serialize
  @State(Scope.Benchmark)
  class FormattingSetup {

    var obj: JObject = null
    
    @Setup(Level.Iteration)
    def initAll: Unit = {

      val value1: String = generateString(random.nextInt(256))
      val value2 = random.nextLong()
      val value3: String = generateString(random.nextInt(256))
      val value4 = random.nextLong()
      val value5: String = generateString(random.nextInt(256))
      val value6 = random.nextLong()
      val value7: String = generateString(random.nextInt(256))
      val value8 = random.nextLong()
      val value9: String = generateString(random.nextInt(256))
      val value10 = random.nextLong()
      val value11: String = generateString(random.nextInt(256))
      val value12 = random.nextLong()
      val value13: String = generateString(random.nextInt(256))
      val value14 = random.nextLong()
      val value15: String = generateString(random.nextInt(256))
      val value16 = random.nextLong()
      val value17: String = generateString(random.nextInt(256))
      val value18 = random.nextLong()
      val value19: String = generateString(random.nextInt(256))
      val value20 = random.nextLong()
      val value21: String = generateString(random.nextInt(256))
      val value22 = random.nextLong()
      val value23: String = generateString(random.nextInt(256))
      val value24 = random.nextLong()

      obj = ("arg1" -> value1) ~ ("arg2" -> value2) ~ ("arg3" -> value3) ~
        ("arg4" -> value4) ~ ("arg5" -> value5) ~ ("arg6" -> value6) ~
        ("arg7" -> value7) ~ ("arg8" -> value8) ~ ("arg9" -> value9) ~
        ("arg10" -> value10) ~ ("arg11" -> value11) ~ ("arg12" -> value12) ~
        ("arg13" -> value13) ~ ("arg14" -> value14) ~ ("arg15" -> value15) ~
        ("arg16" -> value16) ~ ("arg17" -> value17) ~ ("arg18" -> value18) ~
        ("arg19" -> value19) ~ ("arg20" -> value20) ~ ("arg21" -> value21) ~
        ("arg22" -> value22) ~ ("arg23" -> value23) ~ ("arg24" -> value24)
    }
  }
}

class JsonBenchmark {

  import JsonBenchmark._

  @Benchmark
  def testJsonParse(setup: ParsingSetup, bh: Blackhole): Unit =
    parse(setup.str) match {
      case obj: JObject => bh.consume(obj)
      case o => throw new Exception("not a jobject parsed " + o)
    }

  @Benchmark
  def testJsonFormat(setup: FormattingSetup, bh: Blackhole): Unit = 
    bh.consume(compactJson(setup.obj))
}

