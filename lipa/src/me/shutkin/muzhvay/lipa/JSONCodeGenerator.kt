@file:JvmName("JSONCodeGenerator")

package me.shutkin.muzhvay.lipa

import java.io.FileInputStream
import java.util.*

fun main(args: Array<String>) {
  val stream = FileInputStream(args[0])
  val lipa = parse(stream)
  stream.close()
  if (lipa.schemeName != "json-scheme")
    throw Exception("Lipa file must use [json-scheme]")
  if (lipa.rootNode == null)
    throw Exception("No data in lipa file")

  val kotlinCode = generateJSONDeserializer(lipa.rootNode)
  println(kotlinCode)
}

fun generateJSONDeserializer(lipa: LipaNode): String {
  val packageName = lipa.children.find { it.key == "package" }?.values?.get(0) ?: ""
  val className = lipa.children.find { it.key == "class" }?.values?.get(0) ?: "JSON"
  val definitions = lipa.children.filter { it.key == "definition" }

  val propertiesMap = HashMap<Int, LipaNode>()
  val arraysMap = HashMap<Int, LipaNode>()
  var propertyKey = 1
  val definitionKeys = LinkedList<String>()
  for (definition in definitions) {
    val idKeys = ArrayList<MutableMap<Char, Int>>()
    for (property in definition.children) {
      val propertyName = getPropertyName(property) + '"'
      var i = 0
      propertyName.indices.forEach {
        if (idKeys.size > i) {
          if (idKeys[i].containsKey(propertyName[it]))
            i = idKeys[i][propertyName[it]]!!
          else {
            idKeys[i][propertyName[it]] = idKeys.size
            i = idKeys.size
          }
        } else
          idKeys.add(mutableMapOf(propertyName[it] to if (propertyName.length > it + 1) ++i else -1 - propertyKey))
      }
      propertiesMap[propertyKey++] = property
      if (property.key == "array") {
        arraysMap[propertyKey++] = property
      }
    }
    definitionKeys.add("          ${definitionKeys.size} -> when (keyState) {\n" +
            idKeys.mapIndexed { index, map ->
              "            ${-1 - index} -> when(c) { " + map.map { "'${it.key}' -> ${-1 - it.value}; " }.joinToString(separator = "") + "else -> 0 }"
            }.joinToString(separator = "\n") + "\n            else -> 0\n          }")
  }
  val propertiesKeysCount = propertiesMap.count() + arraysMap.count()

  return "/* Generated by Muzhvay */\n\n" +
          "package $packageName\n\n" +
          "import java.io.InputStream\n" +
          "import java.util.*\n\n" +
          "class $className {\n" +
          c1 + definitionKeys.joinToString(separator = "\n") + c2 +
          createProcessor(propertiesMap.filter { isNumericType(it.value.key) }.map { it.key } +
                  arraysMap.filter { isNumericType(it.value.values.first()) }.map { it.key }, numericValueProcessor) +
          createProcessor(propertiesMap.filter { isStringType(it.value.key) }.map { it.key } +
                  arraysMap.filter { isStringType(it.value.values.first()) }.map { it.key }, stringValueProcessor) +
          createProcessor(propertiesMap.filter { isBooleanType(it.value.key) }.map { it.key } +
                  arraysMap.filter { isBooleanType(it.value.values.first()) }.map { it.key }, booleanValueProcessor) +
          createProcessor(listOf(0) + propertiesMap.filter { it.value.key == "object" }.map { it.key } +
                  arraysMap.filter { !commonTypes.contains(it.value.values.first()) }.map { it.key }, getChildProcessor(propertiesKeysCount)) +
          createProcessor(propertiesMap.filter { it.value.key == "array" && commonTypes.contains(it.value.values.first()) }.map { it.key }, primitivesArrayProcessor) +
          createProcessor(propertiesMap.filter { it.value.key == "array" && !commonTypes.contains(it.value.values.first()) }.map { it.key }, objectsArrayProcessor) +
          c3 + (0..propertiesKeysCount).map { getObjectReferenceType(definitions, propertiesMap, arraysMap, it) }.joinToString(separator = ", ") + ")\n\n" +
          definitions.joinToString(separator = "\n", transform = { generateObjectCreator(it, propertiesMap, arraysMap) }) + "\n" +
          objectReader +
          definitions.joinToString(separator = "\n", transform = { generateDataClass(it) }) + "\n\n" +
          definitions.filter { it.attributes.contains("read") }.joinToString(separator = "\n", transform = {
            "  fun read${it.values.first()}(stream: InputStream): ${it.values.first()}? = create${it.values.first()}(read(stream, ${definitions.indexOf(it)}))"
          }) + "\n}"
}

private fun createProcessor(keys: List<Int>, processor: String) = if (keys.isEmpty()) "" else "          ${keys.joinToString()}$processor"

private fun getObjectReferenceType(definitions: List<LipaNode>, properties: Map<Int, LipaNode>, arrays: Map<Int, LipaNode>, key: Int): Int {
  val property = (if (properties.containsKey(key)) properties[key] else if (arrays.containsKey(key)) arrays[key] else null)
          ?: return -1
  if (property.key != "object" && property.key != "array")
    return -1
  val type = property.values.first()
  if (commonTypes.contains(type))
    return -1
  return definitions.indexOfFirst { it.values.first() == type }
}

private fun generateObjectCreator(definition: LipaNode, properties: Map<Int, LipaNode>, arrays: Map<Int, LipaNode>) = "  private fun " +
        "create${definition.values.first()}(obj: JSONObject?):${definition.values.first()}? {\n" +
        "    return if (obj == null) null else if (obj.values == null || obj.children == null) throw Exception(\"Invalid object\") else\n" +
        "      ${definition.values.first()}(\n" +
        definition.children.joinToString(separator = ",\n", transform = { "            " + generatePropertyInit(it, properties, arrays) }) +
        "\n      )\n  }\n"

private fun generatePropertyInit(property: LipaNode, properties: Map<Int, LipaNode>, arrays: Map<Int, LipaNode>): String {
  if (commonTypes.contains(property.key)) {
    val valueIndex = properties.filter { it.value === property }.map { it.key - 1 }.getOrElse(0, { -1 })
    return "if (obj.values[$valueIndex] != null) obj.values[$valueIndex]!!${getPropertyDeserializer(property.key)} else null"
  }

  if (property.key == "object") {
    val valueIndex = properties.filter { it.value === property }.map { it.key }.getOrElse(0, { -1 })
    return "if (obj.children[$valueIndex] != null) create${property.values.first()}(obj.children[$valueIndex]) else null"
  }

  val valueIndex = arrays.filter { it.value === property }.map { it.key - 1 }.getOrElse(0, { -1 })
  val type = property.values.first()
  return if (commonTypes.contains(type))
    "if (obj.children[$valueIndex] != null) obj.children[$valueIndex]!!.primitivesArray!!.filter { it.isNotEmpty() }.map { it${getPropertyDeserializer(type)} }.${getPropertyArrayCollector(type)} else null"
  else
    "if (obj.children[$valueIndex] != null) obj.children[$valueIndex]!!.objectsArray!!.map { create$type(it)!! }.toTypedArray() else null"
}

private fun isNumericType(type: String) = type == "int" || type == "long" || type == "float" || type == "double"
private fun isStringType(type: String) = type == "string" || type == "str"
private fun isBooleanType(type: String) = type == "boolean" || type == "bool"

private const val c1 = "  private data class JSONObject(val type: Int, val parent: JSONObject?, val key: Int,\n" +
        "                                val values: Array<String?>? = null,\n" +
        "                                val primitivesArray: MutableList<String>? = null,\n" +
        "                                val objectsArray: MutableList<JSONObject>? = null,\n" +
        "                                val children: Array<JSONObject?>? = null) {\n" +
        "    override fun toString(): String {\n" +
        "      return \"JSONObject(type=\$type, key=\$key, values=\$values, primitivesArray=\$primitivesArray, objectsArray=\$objectsArray, children=\$children)\"\n" +
        "    }\n" +
        "\n" +
        "    override fun equals(other: Any?): Boolean {\n" +
        "      if (this === other) return true\n" +
        "      if (javaClass != other?.javaClass) return false\n" +
        "      other as JSONObject\n" +
        "      if (type != other.type) return false\n" +
        "      if (parent != other.parent) return false\n" +
        "      if (key != other.key) return false\n" +
        "      return true\n" +
        "    }\n" +
        "\n" +
        "    override fun hashCode(): Int {\n" +
        "      var result = type\n" +
        "      result = 31 * result + (parent?.hashCode() ?: 0)\n" +
        "      result = 31 * result + key\n" +
        "      return result\n" +
        "    }\n" +
        "  }\n" +
        "\n" +
        "  private val rootObject = JSONObject(-1, null, -1, children = Array(1, {null}))\n" +
        "  private var obj = rootObject\n" +
        "  private var keyState: Int? = null\n" +
        "  private var valueState: Int? = null\n" +
        "  private val valueBuf = CharArray(4096)\n" +
        "  private var valueBufIndex = 0\n" +
        "  private var value: String? = null\n" +
        "  private var repeat: Boolean = false\n" +
        "  private val unicode = CharArray(4)\n" +
        "  private var unicodeIndex = 0\n" +
        "\n" +
        "  private fun addValue() {\n" +
        "    val v = if (value != null) value + String(valueBuf, 0, valueBufIndex) else String(valueBuf, 0, valueBufIndex)\n" +
        "    if (obj.values != null)\n" +
        "      obj.values!![keyState!! - 1] = v\n" +
        "    else if (obj.primitivesArray != null)\n" +
        "      obj.primitivesArray!!.add(v)\n" +
        "    valueBufIndex = 0\n" +
        "    value = null\n" +
        "  }\n" +
        "\n" +
        "  private fun processChar(c: Char) {\n" +
        "    repeat = false\n" +
        "    if (keyState != null) {\n" +
        "      if (keyState!! < 0) {\n" +
        "        keyState = when (obj.type) {\n"
private const val c2 = "\n          else -> 0\n" +
        "        }\n" +
        "        if (keyState == 0) throw Exception(\"Invalid key character '\$c'\")\n" +
        "      } else {\n" +
        "        if (valueState == null) {\n" +
        "          when (c) {\n" +
        "            ':' -> valueState = 0\n" +
        "            else -> if (c != ' ' && c != '\\n' && c != '\\r' && c != '\\t') throw Exception(\"Invalid character '\$c' after a key\")\n" +
        "          }\n" +
        "        } else {\n" +
        "          if (valueState == 0 && (c == 'n' || c == 'N'))\n" +
        "            valueState = -1\n" +
        "          else if (valueState!! < 0) {\n" +
        "            when (valueState) {\n" +
        "              -1 -> if (c == 'u' || c == 'U') valueState = -2 else throw Exception(\"Invalid NULL value character '\$c'\")\n" +
        "              -2 -> if (c == 'l' || c == 'L') valueState = -3 else throw Exception(\"Invalid NULL value character '\$c'\")\n" +
        "              -3 -> if (c == 'l' || c == 'L') {\n" +
        "                valueState = null; keyState = null\n" +
        "              } else throw Exception(\"Invalid NULL value character '\$c'\")\n" +
        "            }\n" +
        "          } else when (keyState) {\n"
private const val numericValueProcessor = " -> when (valueState) { // Numeric value\n" +
        "              0 -> when (c) {\n" +
        "                in '0'..'9', '-', '.' -> { valueState = 1; valueBuf[valueBufIndex++] = c }\n" +
        "                ',', '}', ']' -> { addValue(); keyState = null; valueState = null; repeat = true }\n" +
        "                else -> if (c != ' ' && c != '\\n' && c != '\\r' && c != '\\t') throw Exception(\"Not a numeric character '\$c'\")\n" +
        "              }\n" +
        "              1 -> when (c) {\n" +
        "                in '0'..'9', '-', '.', '+', 'e', 'E' -> valueBuf[valueBufIndex++] = c\n" +
        "                else -> { addValue(); keyState = null; valueState = null; repeat = true }\n" +
        "              }\n" +
        "              else -> throw Exception(\"Invalid numeric value state\")\n" +
        "            }\n"
private const val stringValueProcessor = " -> when (valueState) { // String value\n" +
        "              0 -> when (c) {\n" +
        "                '\"' -> valueState = 1\n" +
        "                else -> if (c != ' ' && c != '\\n' && c != '\\r' && c != '\\t') throw Exception(\"Invalid opening string character '\$c'\")\n" +
        "              }\n" +
        "              1 -> when (c) {\n" +
        "                '\\\\' -> valueState = 2\n" +
        "                '\"' -> { addValue(); keyState = null; valueState = null }\n" +
        "                else -> valueBuf[valueBufIndex++] = c\n" +
        "              }\n" +
        "              2 -> when (c) {\n" +
        "                '\"' -> { valueBuf[valueBufIndex++] = '\"'; valueState = 1 }\n" +
        "                '\\\\' -> { valueBuf[valueBufIndex++] = '\\\\'; valueState = 1 }\n" +
        "                '/' -> { valueBuf[valueBufIndex++] = '/'; valueState = 1 }\n" +
        "                'b' -> { valueBuf[valueBufIndex++] = '\\b'; valueState = 1 }\n" +
        "                'n' -> { valueBuf[valueBufIndex++] = '\\n'; valueState = 1 }\n" +
        "                'r' -> { valueBuf[valueBufIndex++] = '\\r'; valueState = 1 }\n" +
        "                't' -> { valueBuf[valueBufIndex++] = '\\t'; valueState = 1 }\n" +
        "                'u' -> { valueState = 3; unicodeIndex = 0 }\n" +
        "                else -> throw Exception(\"Invalid escape sequence '\\\\\$c'\")\n" +
        "              }\n" +
        "              3 -> {\n" +
        "                unicode[unicodeIndex++] = c\n" +
        "                if (unicodeIndex == 4) {\n" +
        "                  valueBuf[valueBufIndex++] = String(unicode).toInt(16).toChar()\n" +
        "                  valueState = 1\n" +
        "                }\n" +
        "              }\n" +
        "              else -> throw Exception(\"Invalid string value state\")\n" +
        "            }\n"
private const val booleanValueProcessor = " -> when (valueState) { // Boolean value\n" +
        "              0 -> when (c) {\n" +
        "                't', 'T' -> valueState = 1\n" +
        "                'f', 'F' -> valueState = 4\n" +
        "                else -> if (c != ' ' && c != '\\n' && c != '\\r' && c != '\\t') throw Exception(\"Invalid boolean character '\$c'\")\n" +
        "              }\n" +
        "              1 -> if (c == 'r' || c == 'R') valueState = 2 else throw Exception(\"Invalid boolean character '\$c'\")\n" +
        "              2 -> if (c == 'u' || c == 'U') valueState = 3 else throw Exception(\"Invalid boolean character '\$c'\")\n" +
        "              3 -> if (c == 'e' || c == 'E') {\n" +
        "                valueBuf[valueBufIndex++] = '1'; addValue(); keyState = null; valueState = null\n" +
        "              } else throw Exception(\"Invalid boolean character '\$c'\")\n" +
        "              4 -> if (c == 'a' || c == 'A') valueState = 5 else throw Exception(\"Invalid boolean character '\$c'\")\n" +
        "              5 -> if (c == 'l' || c == 'L') valueState = 6 else throw Exception(\"Invalid boolean character '\$c'\")\n" +
        "              6 -> if (c == 's' || c == 'S') valueState = 7 else throw Exception(\"Invalid boolean character '\$c'\")\n" +
        "              7 -> if (c == 'e' || c == 'E') {\n" +
        "                valueBuf[valueBufIndex++] = '0'; addValue(); keyState = null; valueState = null\n" +
        "              } else throw Exception(\"Invalid boolean character '\$c'\")\n" +
        "              else -> throw Exception(\"Invalid boolean value state\")\n" +
        "            }\n"

private fun getChildProcessor(keys: Int) = " -> when (c) { // Child object\n" +
        "              '{' -> {\n" +
        "                val newObj = JSONObject(childrenTypes[keyState!!], obj, keyState!!, Array($keys, { null }), children = Array(${keys + 1}, { null }))\n" +
        "                if (obj.children != null)\n" +
        "                  obj.children!![keyState!!] = newObj\n" +
        "                else if (obj.objectsArray != null)\n" +
        "                  obj.objectsArray!!.add(newObj)\n" +
        "                obj = newObj; keyState = null; valueState = null\n" +
        "              }\n" +
        "              else -> if (c != ' ' && c != '\\n' && c != '\\r' && c != '\\t') throw Exception(\"Invalid character '\$c' instead of child object declaration\")\n" +
        "            }\n"

private const val primitivesArrayProcessor = " -> when (c) { // Primitives array\n" +
        "              '[' -> {\n" +
        "                val array = JSONObject(-1, obj, keyState!! + 1, primitivesArray = LinkedList())\n" +
        "                obj.children!![keyState!!] = array\n" +
        "                obj = array; keyState = array.key; valueState = 0\n" +
        "              }\n" +
        "              else -> if (c != ' ' && c != '\\n' && c != '\\r' && c != '\\t') throw Exception(\"Invalid character '\$c' instead of array declaration\")\n" +
        "            }\n"
private const val objectsArrayProcessor = " -> when (c) { // Objects array\n" +
        "              '[' -> {\n" +
        "                val array = JSONObject(-1, obj, keyState!! + 1, objectsArray = LinkedList())\n" +
        "                obj.children!![keyState!!] = array\n" +
        "                obj = array; keyState = array.key; valueState = 0\n" +
        "              }\n" +
        "              else -> if (c != ' ' && c != '\\n' && c != '\\r' && c != '\\t') throw Exception(\"Invalid character '\$c' instead of array declaration\")\n" +
        "            }\n"
private const val c3 = "            else -> throw Exception(\"Invalid key state\")\n" +
        "          }\n" +
        "        }\n" +
        "      }\n" +
        "    } else when (c) {\n" +
        "      ',' -> if (obj.primitivesArray != null || obj.objectsArray != null) {\n" +
        "        keyState = obj.key; valueState = 0\n" +
        "      }\n" +
        "      '}' -> if (obj.parent != null) obj = obj.parent!! else throw Exception(\"Object closing character '}' outside of object\")\n" +
        "      ']' -> when {\n" +
        "        obj.values != null -> throw Exception(\"Array closing character ']' outside of array\")\n" +
        "        obj.parent != null -> obj = obj.parent!!\n" +
        "        else -> throw Exception(\"Unexpected ']'\")\n" +
        "      }\n" +
        "      '\"' -> keyState = -1\n" +
        "      else -> if (c != ' ' && c != '\\n' && c != '\\r' && c != '\\t') throw Exception(\"Invalid character '\$c' while expecting next key\")\n" +
        "    }\n" +
        "  }\n" +
        "\n" +
        "  private val childrenTypes = arrayOf("
private const val objectReader = "  private fun read(stream: InputStream, type: Int): JSONObject? {\n" +
        "    childrenTypes[0] = type\n" +
        "    obj = rootObject; keyState = 0; valueState = 0\n" +
        "    while (true) {\n" +
        "      val c = stream.read()\n" +
        "      if (c < 0) break\n" +
        "      processChar(c.toChar())\n" +
        "      while (repeat)\n" +
        "        processChar(c.toChar())\n" +
        "      if (valueBufIndex == valueBuf.size) {\n" +
        "        value = (value ?: \"\") + String(valueBuf)\n" +
        "        valueBufIndex = 0\n" +
        "      }\n" +
        "    }\n" +
        "    return rootObject.children!![0]\n" +
        "  }\n\n"

private fun generateDataClass(definition: LipaNode) = "  data class ${definition.values[0]}(${definition.children.joinToString(
        transform = { "val ${getPropertyName(it)}: ${getPropertyType(it)}?" }, separator = ", ")})"

private fun getPropertyDeserializer(propertyType: String) = when (propertyType) {
  "int" -> ".toInt()"
  "long" -> ".toLong()"
  "float" -> ".toFloat()"
  "double" -> ".toDouble()"
  "string", "str" -> ""
  "boolean", "bool" -> " == \"1\""
  else -> throw Exception("Can't find deserializer for type '$propertyType'")
}

private fun getPropertyArrayCollector(propertyType: String) = when (propertyType) {
  "int" -> "toIntArray()"
  "long" -> "toLongArray()"
  "float" -> "toFloatArray()"
  "double" -> "toDoubleArray()"
  "boolean", "bool" -> "toBooleanArray()"
  else -> "toTypedArray()"
}

private val commonTypes = setOf("int", "long", "float", "double", "string", "str", "boolean", "bool")

private fun getPropertyType(property: LipaNode) = when {
  commonTypes.contains(property.key) -> getCommonType(property.key)
  property.key == "object" -> property.values[0]
  property.key == "array" -> when (property.values[0]) {
    "int" -> "IntArray"
    "long" -> "LongArray"
    "float" -> "FloatArray"
    "double" -> "DoubleArray"
    "boolean", "bool" -> "BooleanArray"
    else -> "Array<${property.values[0]}>"
  }
  else -> throw Exception("Unknown type '${property.key}'")
}

private fun getCommonType(propertyType: String) = when (propertyType) {
  "int" -> "Int"
  "long" -> "Long"
  "float" -> "Float"
  "double" -> "Double"
  "string", "str" -> "String"
  "boolean", "bool" -> "Boolean"
  else -> throw Exception("Unknown type '$propertyType'")
}

private fun getPropertyName(property: LipaNode) = property.values[if (commonTypes.contains(property.key)) 0 else 1]
