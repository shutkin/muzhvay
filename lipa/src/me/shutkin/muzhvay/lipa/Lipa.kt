@file:JvmName("Lipa")
package me.shutkin.muzhvay.lipa

import java.io.FileInputStream
import java.io.InputStream

data class LipaNode(val parent: LipaNode?, val key: String, val values: List<String>,
                    val attributes: Set<String>,
                    val children: List<LipaNode>) {
  override fun toString(): String {
    return "LipaNode(key='$key', values=$values, attributes=$attributes, children=$children)"
  }
}
data class LipaFile(val schemeName: String?, val rootNode: LipaNode?)

private enum class ParseState { ROOT, SCHEME_NAME, KEY_START, KEY_BODY, VALUE_START, VALUE_BODY, ATTRIBUTE_START, ATTRIBUTE_BODY }

private data class ParserContext(val state: ParseState, val parent: LipaNode?, val key: String? = null,
                                 val values: List<String>? = null, val curValue: String? = null,
                                 val attributes: Set<String>? = null, val curAttribute: String? = null,
                                 val schemeName: String? = null, val repeat: Boolean = false)

private fun isFirstKeyChar(c: Char) = c in 'A'..'Z' || c in 'a'..'z'
private fun isKeyValueChar(c: Char) = c in 'A'..'Z' || c in 'a'..'z' || c in '0'..'1' || c == '_' || c == '-' || c == '.' || c in '0'..'9'

private fun processChar(c: Char, context: ParserContext): ParserContext {
  if (c == ',') {
    createNewNode(context)
    return ParserContext(ParseState.KEY_START, context.parent)
  } else if (c == '}') {
    createNewNode(context)
    return ParserContext(ParseState.KEY_START, if (context.parent != null) context.parent.parent else null)
  }

  when (context.state) {
    ParseState.ROOT ->
      if (c == '[')
        return ParserContext(ParseState.SCHEME_NAME, context.parent)
      else if (isFirstKeyChar(c))
        return ParserContext(ParseState.KEY_BODY, context.parent)

    ParseState.SCHEME_NAME ->
      return if (c == ']')
        ParserContext(ParseState.KEY_START, context.parent)
      else
        ParserContext(ParseState.SCHEME_NAME, context.parent, schemeName = (context.schemeName ?: "") + c)

    ParseState.KEY_START ->
      if (isKeyValueChar(c))
        return ParserContext(ParseState.KEY_BODY, context.parent, repeat = true)

    ParseState.KEY_BODY ->
      return when {
        isKeyValueChar(c) -> ParserContext(ParseState.KEY_BODY, context.parent, key = (context.key
                ?: "") + c)
        else -> ParserContext(ParseState.VALUE_START, context.parent, key = context.key, repeat = true)
      }

    ParseState.VALUE_START ->
      when {
        isKeyValueChar(c) -> return ParserContext(ParseState.VALUE_BODY, context.parent, key = context.key,
                values = context.values, attributes = context.attributes, repeat = true)
        c == '<' -> return ParserContext(ParseState.ATTRIBUTE_START, context.parent, key = context.key,
                values = context.values, attributes = context.attributes)
        c == '{' -> return ParserContext(ParseState.KEY_START, createNewNode(context))
      }

    ParseState.VALUE_BODY ->
      return when {
        isKeyValueChar(c) -> ParserContext(ParseState.VALUE_BODY, context.parent, key = context.key,
                values = context.values, curValue = (context.curValue ?: "") + c, attributes = context.attributes)
        else -> ParserContext(ParseState.VALUE_START, context.parent, key = context.key,
                values = if (context.curValue != null) (context.values ?: emptyList()) + context.curValue else context.values,
                attributes = context.attributes, repeat = true)
      }

    ParseState.ATTRIBUTE_START ->
      when {
        isKeyValueChar(c) -> return ParserContext(ParseState.ATTRIBUTE_BODY, context.parent, key = context.key,
                values = context.values, attributes = context.attributes, repeat = true)
        c == '>' -> return ParserContext(ParseState.VALUE_START, context.parent, key = context.key,
                values = context.values, attributes = context.attributes)
      }

    ParseState.ATTRIBUTE_BODY ->
      return when {
        isKeyValueChar(c) -> ParserContext(ParseState.ATTRIBUTE_BODY, context.parent, key = context.key, values = context.values,
                attributes = context.attributes, curAttribute = (context.curAttribute ?: "") + c)
        else -> ParserContext(ParseState.ATTRIBUTE_START, context.parent, key = context.key, values = context.values,
                attributes = if (context.curAttribute != null) (context.attributes ?: emptySet()) + context.curAttribute else context.attributes,
                repeat = true)
      }
  }
  if (c != '\n' && c != '\r' && c != '\t' && c != ' ')
    throw Exception("Invalid character '$c'")
  return ParserContext(context.state, context.parent, context.key, context.values, context.curValue, context.attributes, context.curAttribute)
}

private fun createNewNode(context: ParserContext): LipaNode? {
  if (context.key == null)
    return null
  val values = context.values ?: emptyList()
  val newNode = LipaNode(context.parent, context.key, if (context.curValue != null) values + context.curValue else values,
          context.attributes ?: emptySet(), ArrayList())
  if (context.parent != null && context.parent.children is ArrayList)
    context.parent.children.add(newNode)
  return newNode
}

fun parse(stream: InputStream): LipaFile {
  var context = ParserContext(ParseState.ROOT, null)
  var rootNode: LipaNode? = null
  var schemeName: String? = null
  var c = 0
  while (c >= 0) {
    c = stream.read()
    if (c > 0) {
      context = processChar(c.toChar(), context)
      while (context.repeat)
        context = processChar(c.toChar(), context)
    }
    if (rootNode == null && context.parent != null)
      rootNode = context.parent
    if (context.schemeName != null)
      schemeName = context.schemeName
  }
  return LipaFile(schemeName, rootNode)
}

fun main(args: Array<String>) {
  val stream = FileInputStream(args[0])
  val lipa = parse(stream)
  println(lipa)
  stream.close()
}
