/* Generated by Muzhvay
   https://github.com/shutkin/muzhvay */

package me.shutkin.muzhvay.testjson

import java.io.InputStream
import java.util.*

class ParserKt {
  private data class JSONObject(val type: Int, val parent: JSONObject?, val key: Int,
                                val values: Array<String?>? = null,
                                val primitivesArray: MutableList<String>? = null,
                                val objectsArray: MutableList<JSONObject>? = null,
                                val children: Array<JSONObject?>? = null) {
    override fun toString(): String {
      return "JSONObject(type=$type, key=$key, values=$values, primitivesArray=$primitivesArray, objectsArray=$objectsArray, children=$children)"
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false
      other as JSONObject
      if (type != other.type) return false
      if (parent != other.parent) return false
      if (key != other.key) return false
      return true
    }

    override fun hashCode(): Int {
      var result = type
      result = 31 * result + (parent?.hashCode() ?: 0)
      result = 31 * result + key
      return result
    }
  }

  private val rootObject = JSONObject(-1, null, -1, children = Array(1, {null}))
  private var obj = rootObject
  private var keyState: Int? = null
  private var valueState: Int? = null
  private val valueBuf = CharArray(4096)
  private var valueBufIndex = 0
  private var value: String? = null
  private var repeat: Boolean = false
  private val unicode = CharArray(4)
  private var unicodeIndex = 0

  private fun addValue() {
    val v = if (value != null) value + String(valueBuf, 0, valueBufIndex) else String(valueBuf, 0, valueBufIndex)
    if (obj.values != null)
      obj.values!![keyState!! - 1] = v
    else if (obj.primitivesArray != null)
      obj.primitivesArray!!.add(v)
    valueBufIndex = 0
    value = null
  }

  private fun processChar(c: Char) {
    repeat = false
    if (keyState != null) {
      if (keyState!! < 0) {
        keyState = when (obj.type) {
          0 -> when (keyState) {
            -1 -> when(c) { 'i' -> -2; 't' -> -4; 'c' -> -9; 'n' -> -17; else -> 0 }
            -2 -> when(c) { 'd' -> -3; else -> 0 }
            -3 -> when(c) { '"' -> 1; else -> 0 }
            -4 -> when(c) { 'i' -> -5; else -> 0 }
            -5 -> when(c) { 't' -> -6; else -> 0 }
            -6 -> when(c) { 'l' -> -7; else -> 0 }
            -7 -> when(c) { 'e' -> -8; else -> 0 }
            -8 -> when(c) { '"' -> 2; else -> 0 }
            -9 -> when(c) { 'h' -> -10; else -> 0 }
            -10 -> when(c) { 'i' -> -11; else -> 0 }
            -11 -> when(c) { 'l' -> -12; else -> 0 }
            -12 -> when(c) { 'd' -> -13; else -> 0 }
            -13 -> when(c) { 'r' -> -14; else -> 0 }
            -14 -> when(c) { 'e' -> -15; else -> 0 }
            -15 -> when(c) { 'n' -> -16; else -> 0 }
            -16 -> when(c) { '"' -> 3; else -> 0 }
            -17 -> when(c) { 'e' -> -18; else -> 0 }
            -18 -> when(c) { 'x' -> -19; else -> 0 }
            -19 -> when(c) { 't' -> -20; else -> 0 }
            -20 -> when(c) { '"' -> 5; else -> 0 }
            else -> 0
          }
          1 -> when (keyState) {
            -1 -> when(c) { 'i' -> -2; 'n' -> -7; 'd' -> -11; 'f' -> -15; else -> 0 }
            -2 -> when(c) { 'n' -> -3; 's' -> -21; else -> 0 }
            -3 -> when(c) { 'd' -> -4; else -> 0 }
            -4 -> when(c) { 'e' -> -5; else -> 0 }
            -5 -> when(c) { 'x' -> -6; else -> 0 }
            -6 -> when(c) { '"' -> 6; else -> 0 }
            -7 -> when(c) { 'a' -> -8; else -> 0 }
            -8 -> when(c) { 'm' -> -9; else -> 0 }
            -9 -> when(c) { 'e' -> -10; else -> 0 }
            -10 -> when(c) { '"' -> 7; else -> 0 }
            -11 -> when(c) { 'a' -> -12; else -> 0 }
            -12 -> when(c) { 't' -> -13; else -> 0 }
            -13 -> when(c) { 'a' -> -14; else -> 0 }
            -14 -> when(c) { '"' -> 8; else -> 0 }
            -15 -> when(c) { 'a' -> -16; else -> 0 }
            -16 -> when(c) { 'c' -> -17; else -> 0 }
            -17 -> when(c) { 't' -> -18; else -> 0 }
            -18 -> when(c) { 'o' -> -19; else -> 0 }
            -19 -> when(c) { 'r' -> -20; else -> 0 }
            -20 -> when(c) { '"' -> 10; else -> 0 }
            -21 -> when(c) { 'A' -> -22; else -> 0 }
            -22 -> when(c) { 'l' -> -23; else -> 0 }
            -23 -> when(c) { 'i' -> -24; else -> 0 }
            -24 -> when(c) { 'v' -> -25; else -> 0 }
            -25 -> when(c) { 'e' -> -26; else -> 0 }
            -26 -> when(c) { '"' -> 11; else -> 0 }
            else -> 0
          }
          else -> 0
        }
        if (keyState == 0) throw Exception("Invalid key character '$c'")
      } else {
        if (valueState == null) {
          when (c) {
            ':' -> valueState = 0
            else -> if (c != ' ' && c != '\n' && c != '\r' && c != '\t') throw Exception("Invalid character '$c' after a key")
          }
        } else {
          if (valueState == 0 && (c == 'n' || c == 'N'))
            valueState = -1
          else if (valueState!! < 0) {
            when (valueState) {
              -1 -> if (c == 'u' || c == 'U') valueState = -2 else throw Exception("Invalid NULL value character '$c'")
              -2 -> if (c == 'l' || c == 'L') valueState = -3 else throw Exception("Invalid NULL value character '$c'")
              -3 -> if (c == 'l' || c == 'L') {
                valueState = null; keyState = null
              } else throw Exception("Invalid NULL value character '$c'")
            }
          } else when (keyState) {
            1, 6, 10, 9 -> when (valueState) { // Numeric value
              0 -> when (c) {
                in '0'..'9', '-', '.' -> { valueState = 1; valueBuf[valueBufIndex++] = c }
                ',', '}', ']' -> { addValue(); keyState = null; valueState = null; repeat = true }
                else -> if (c != ' ' && c != '\n' && c != '\r' && c != '\t') throw Exception("Not a numeric character '$c'")
              }
              1 -> when (c) {
                in '0'..'9', '-', '.', '+', 'e', 'E' -> valueBuf[valueBufIndex++] = c
                else -> { addValue(); keyState = null; valueState = null; repeat = true }
              }
              else -> throw Exception("Invalid numeric value state")
            }
            2, 7 -> when (valueState) { // String value
              0 -> when (c) {
                '"' -> valueState = 1
                else -> if (c != ' ' && c != '\n' && c != '\r' && c != '\t') throw Exception("Invalid opening string character '$c'")
              }
              1 -> when (c) {
                '\\' -> valueState = 2
                '"' -> { addValue(); keyState = null; valueState = null }
                else -> valueBuf[valueBufIndex++] = c
              }
              2 -> when (c) {
                '"' -> { valueBuf[valueBufIndex++] = '"'; valueState = 1 }
                '\\' -> { valueBuf[valueBufIndex++] = '\\'; valueState = 1 }
                '/' -> { valueBuf[valueBufIndex++] = '/'; valueState = 1 }
                'b' -> { valueBuf[valueBufIndex++] = '\b'; valueState = 1 }
                'n' -> { valueBuf[valueBufIndex++] = '\n'; valueState = 1 }
                'r' -> { valueBuf[valueBufIndex++] = '\r'; valueState = 1 }
                't' -> { valueBuf[valueBufIndex++] = '\t'; valueState = 1 }
                'u' -> { valueState = 3; unicodeIndex = 0 }
                else -> throw Exception("Invalid escape sequence '\\$c'")
              }
              3 -> {
                unicode[unicodeIndex++] = c
                if (unicodeIndex == 4) {
                  valueBuf[valueBufIndex++] = String(unicode).toInt(16).toChar()
                  valueState = 1
                }
              }
              else -> throw Exception("Invalid string value state")
            }
            11 -> when (valueState) { // Boolean value
              0 -> when (c) {
                't', 'T' -> valueState = 1
                'f', 'F' -> valueState = 4
                else -> if (c != ' ' && c != '\n' && c != '\r' && c != '\t') throw Exception("Invalid boolean character '$c'")
              }
              1 -> if (c == 'r' || c == 'R') valueState = 2 else throw Exception("Invalid boolean character '$c'")
              2 -> if (c == 'u' || c == 'U') valueState = 3 else throw Exception("Invalid boolean character '$c'")
              3 -> if (c == 'e' || c == 'E') {
                valueBuf[valueBufIndex++] = '1'; addValue(); keyState = null; valueState = null
              } else throw Exception("Invalid boolean character '$c'")
              4 -> if (c == 'a' || c == 'A') valueState = 5 else throw Exception("Invalid boolean character '$c'")
              5 -> if (c == 'l' || c == 'L') valueState = 6 else throw Exception("Invalid boolean character '$c'")
              6 -> if (c == 's' || c == 'S') valueState = 7 else throw Exception("Invalid boolean character '$c'")
              7 -> if (c == 'e' || c == 'E') {
                valueBuf[valueBufIndex++] = '0'; addValue(); keyState = null; valueState = null
              } else throw Exception("Invalid boolean character '$c'")
              else -> throw Exception("Invalid boolean value state")
            }
            0, 5, 4 -> when (c) { // Child object
              '{' -> {
                val newObj = JSONObject(childrenTypes[keyState!!], obj, keyState!!, Array(11, { null }), children = Array(12, { null }))
                if (obj.children != null)
                  obj.children!![keyState!!] = newObj
                else if (obj.objectsArray != null)
                  obj.objectsArray!!.add(newObj)
                obj = newObj; keyState = null; valueState = null
              }
              else -> if (c != ' ' && c != '\n' && c != '\r' && c != '\t') throw Exception("Invalid character '$c' instead of child object declaration")
            }
            8 -> when (c) { // Primitives array
              '[' -> {
                val array = JSONObject(-1, obj, keyState!! + 1, primitivesArray = LinkedList())
                obj.children!![keyState!!] = array
                obj = array; keyState = array.key; valueState = 0
              }
              else -> if (c != ' ' && c != '\n' && c != '\r' && c != '\t') throw Exception("Invalid character '$c' instead of array declaration")
            }
            3 -> when (c) { // Objects array
              '[' -> {
                val array = JSONObject(-1, obj, keyState!! + 1, objectsArray = LinkedList())
                obj.children!![keyState!!] = array
                obj = array; keyState = array.key; valueState = 0
              }
              else -> if (c != ' ' && c != '\n' && c != '\r' && c != '\t') throw Exception("Invalid character '$c' instead of array declaration")
            }
            else -> throw Exception("Invalid key state")
          }
        }
      }
    } else when (c) {
      ',' -> if (obj.primitivesArray != null || obj.objectsArray != null) {
        keyState = obj.key; valueState = 0
      }
      '}' -> if (obj.parent != null) obj = obj.parent!! else throw Exception("Object closing character '}' outside of object")
      ']' -> when {
        obj.values != null -> throw Exception("Array closing character ']' outside of array")
        obj.parent != null -> obj = obj.parent!!
        else -> throw Exception("Unexpected ']'")
      }
      '"' -> keyState = -1
      else -> if (c != ' ' && c != '\n' && c != '\r' && c != '\t') throw Exception("Invalid character '$c' while expecting next key")
    }
  }

  private val childrenTypes = arrayOf(-1, -1, -1, 1, 1, 0, -1, -1, -1, -1, -1, -1)

  private fun createTestObject(obj: JSONObject?): TestObject? {
    return if (obj == null) null else if (obj.values == null || obj.children == null) throw Exception("Invalid object") else
      TestObject(
              if (obj.values[0] != null) obj.values[0]!!.toLong() else null,
              if (obj.values[1] != null) obj.values[1]!! else null,
              if (obj.children[3] != null) obj.children[3]!!.objectsArray!!.map { createTestChildObject(it)!! }.toTypedArray() else null,
              if (obj.children[5] != null) createTestObject(obj.children[5]) else null
      )
  }

  private fun createTestChildObject(obj: JSONObject?): TestChildObject? {
    return if (obj == null) null else if (obj.values == null || obj.children == null) throw Exception("Invalid object") else
      TestChildObject(
              if (obj.values[5] != null) obj.values[5]!!.toInt() else null,
              if (obj.values[6] != null) obj.values[6]!! else null,
              if (obj.children[8] != null) obj.children[8]!!.primitivesArray!!.filter { it.isNotEmpty() }.map { it.toInt() }.toIntArray() else null,
              if (obj.values[9] != null) obj.values[9]!!.toDouble() else null,
              if (obj.values[10] != null) obj.values[10]!! == "1" else null
      )
  }

  private fun read(stream: InputStream, type: Int): JSONObject? {
    childrenTypes[0] = type
    obj = rootObject; keyState = 0; valueState = 0
    while (true) {
      val c = stream.read()
      if (c < 0) break
      processChar(c.toChar())
      while (repeat)
        processChar(c.toChar())
      if (valueBufIndex == valueBuf.size) {
        value = (value ?: "") + String(valueBuf)
        valueBufIndex = 0
      }
    }
    return rootObject.children!![0]
  }

  data class TestObject(val id: Long?, val title: String?, val children: Array<TestChildObject>?, val next: TestObject?)
  data class TestChildObject(val index: Int?, val name: String?, val data: IntArray?, val factor: Double?, val isAlive: Boolean?)

  fun readTestObject(stream: InputStream): TestObject? = createTestObject(read(stream, 0))
}
