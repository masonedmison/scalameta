package scala.meta
package internal
package tokenizers

import scala.util.Try
import LegacyToken._
import Chars._

private[meta] trait LegacyTokenData {
  /** the input that is currently being tokenized */
  var content: Content = null

  /** the next token */
  var token: LegacyToken = EMPTY

  /** the offset of the first character of the current token */
  var offset: Offset = 0

  /** the offset of the character following the token preceding this one */
  var lastOffset: Offset = 0

  /** the offset of the last character of the current token */
  var endOffset: Offset = 0

  /** the name of an identifier */
  var name: String = null

  /** the string value of a literal */
  var strVal: String = null

  /** the base of a number */
  var base: Int = 0

  def copyFrom(td: LegacyTokenData): this.type = {
    this.content = td.content
    this.token = td.token
    this.offset = td.offset
    this.lastOffset = td.lastOffset
    this.endOffset = td.endOffset
    this.name = td.name
    this.strVal = td.strVal
    this.base = td.base
    this
  }

  override def toString = s"{token = $token, position = $offset..$endOffset, lastOffset = $lastOffset, name = $name, strVal = $strVal, base = $base}"

  val reporter: Reporter = Reporter(content)
  import reporter._

  /** Convert current strVal to char value
   */
  def charVal: Char = if (strVal.length > 0) strVal.charAt(0) else 0

  /** Convert current strVal, base to an integer value
   *  This is tricky because of max negative value.
   */
  private def integerVal(limit: BigInt): BigInt = {
    var input = strVal
    if (input.startsWith("0x") || input.startsWith("0X")) input = input.substring(2)
    if (input.endsWith("l") || input.endsWith("L")) input = input.substring(0, input.length - 1)
    var value: BigInt = 0
    val divider = if (base == 10) 1 else 2
    var i = 0
    val len = input.length
    while (i < len) {
      val d = digit2int(input charAt i, base)
      if (d < 0) {
        syntaxError("malformed integer number", at = offset)
      }
      value = value * base + d
      i += 1
    }
    if (value > limit) syntaxError("integer number too large", at = offset)
    value
  }

  /** Convert current strVal, base to double value
  */
  private def floatingVal(limit: Double): Double = {
    val value: Double = java.lang.Double.valueOf(strVal).doubleValue()
    def isDeprecatedForm = {
      val idx = strVal indexOf '.'
      (idx == strVal.length - 1) || (
           (idx >= 0)
        && (idx + 1 < strVal.length)
        && (!Character.isDigit(strVal charAt (idx + 1)))
      )
    }
    if (isDeprecatedForm)
      syntaxError("floating point number is missing digit after dot", at = offset)
    if (value > limit) syntaxError("floating point number too large", at = offset)
    value
  }

  def intVal: BigInt = integerVal(-BigInt(Int.MinValue))
  def longVal: BigInt = integerVal(-BigInt(Long.MinValue))
  def floatVal: Float = floatingVal(Float.MaxValue).toFloat
  def doubleVal: Double = floatingVal(Double.MaxValue)
}
