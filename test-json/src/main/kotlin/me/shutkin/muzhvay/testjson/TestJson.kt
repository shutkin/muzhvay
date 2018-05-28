package me.shutkin.muzhvay.testjson

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper

fun main(args: Array<String>) {
  val json = serializeObject(generateObject())
  println(json)

  val parserKt = ParserKt()
  val objKt = parserKt.readTestObject(json.byteInputStream())
  println(objKt)

  val mapper = ObjectMapper()
  val jsonFactory = JsonFactory()
  val pojoClass = TestPOJO.TestObject().javaClass
  val parser = JsonFactory().createParser(json.byteInputStream())
  val objJackson = mapper.readValue(parser, pojoClass)
  println(objJackson)

  (0 .. 100000).forEach {
    val rnd = serializeObject(generateObject())
    parserKt.readTestObject(rnd.byteInputStream())
    mapper.readValue(jsonFactory.createParser(rnd.byteInputStream()), pojoClass)
  }
  println("start")

  val jacksonStartTime = System.currentTimeMillis()
  (0 .. 100000).forEach {
    val rnd = serializeObject(generateObject())
    mapper.readValue(jsonFactory.createParser(rnd.byteInputStream()), pojoClass)
  }
  println("Jackson: ${System.currentTimeMillis() - jacksonStartTime}")

  val parserKtStartTime = System.currentTimeMillis()
  (0 .. 100000).forEach {
    val rnd = serializeObject(generateObject())
    parserKt.readTestObject(rnd.byteInputStream())
  }
  println("Parser Kt: ${System.currentTimeMillis() - parserKtStartTime}")
}

private fun serializeObject(obj: ParserKt.TestObject): String {
  return "{\"id\" : " + obj.id +
          (if (obj.title != null) ",\"title\" : ${obj.title}" else "") +
          (if (obj.children != null) ",\"children\" : [${obj.children.joinToString { serializeChildObject(it) }} ]" else "") +
          (if (obj.next != null) ", \"next\": ${serializeObject(obj.next)}" else "") + "}"
}

private fun serializeChildObject(obj: ParserKt.TestChildObject) = "{ \"index\" :" + obj.index +
        (if (obj.name != null) ",\"name\": ${obj.name}" else "") +
        (if (obj.data != null) ",\"data\" :[ ${obj.data.joinToString(separator = " ,")}] " else "") +
        (if (obj.factor != null) ",\"factor\": ${obj.factor}" else "") +
        (if (obj.isAlive != null) ",\"isAlive\":${obj.isAlive}" else "") + "}"

private fun generateObject() =
        ParserKt.TestObject(
                (65536.0 * Math.random()).toLong(),
                generateString(),
                Array((4.0 + 16.0 * Math.random()).toInt(), { generateChildObject() }),
                generateNext())

private fun generateNext() =
        if (Math.random() > 0.75)
          null
        else
          ParserKt.TestObject(
                  (65536.0 * Math.random()).toLong(),
                  generateString(),
                  if (Math.random() > 0.33) Array((4.0 + 16.0 * Math.random()).toInt(), { generateChildObject() }) else null,
                  null)

private fun generateChildObject() =
        ParserKt.TestChildObject(
                (32.0 * Math.random()).toInt(),
                generateString(),
                IntArray(if (Math.random() > 0.25) (8.0 + 16.0 * Math.random()).toInt() else 0, { (999999.0 * Math.random()).toInt() - 500000 }),
                Math.random(),
                if (Math.random() > 0.33) Math.random() > 0.5 else null)

private fun generateString() = "\"" + String(CharArray((4.0 + 16.0 * Math.random()).toInt(), { ('a'.toDouble() + 24.0 * Math.random()).toChar() })) + "\\\"\\u2665end\""
