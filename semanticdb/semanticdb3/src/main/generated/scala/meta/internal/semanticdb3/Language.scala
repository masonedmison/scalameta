// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package scala.meta.internal.semanticdb3

sealed trait Language extends _root_.scalapb.GeneratedEnum {
  type EnumType = Language
  def isUnknownLanguage: _root_.scala.Boolean = false
  def isScala: _root_.scala.Boolean = false
  def isJava: _root_.scala.Boolean = false
  def companion: _root_.scalapb.GeneratedEnumCompanion[Language] = scala.meta.internal.semanticdb3.Language
}

object Language extends _root_.scalapb.GeneratedEnumCompanion[Language] {
  implicit def enumCompanion: _root_.scalapb.GeneratedEnumCompanion[Language] = this
  @SerialVersionUID(0L)
  case object UNKNOWN_LANGUAGE extends Language {
    val value = 0
    val index = 0
    val name = "UNKNOWN_LANGUAGE"
    override def isUnknownLanguage: _root_.scala.Boolean = true
  }
  
  @SerialVersionUID(0L)
  case object SCALA extends Language {
    val value = 1
    val index = 1
    val name = "SCALA"
    override def isScala: _root_.scala.Boolean = true
  }
  
  @SerialVersionUID(0L)
  case object JAVA extends Language {
    val value = 2
    val index = 2
    val name = "JAVA"
    override def isJava: _root_.scala.Boolean = true
  }
  
  @SerialVersionUID(0L)
  final case class Unrecognized(value: _root_.scala.Int) extends Language with _root_.scalapb.UnrecognizedEnum
  
  lazy val values = scala.collection.Seq(UNKNOWN_LANGUAGE, SCALA, JAVA)
  def fromValue(value: _root_.scala.Int): Language = value match {
    case 0 => UNKNOWN_LANGUAGE
    case 1 => SCALA
    case 2 => JAVA
    case __other => Unrecognized(__other)
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.EnumDescriptor = Semanticdb3Proto.javaDescriptor.getEnumTypes.get(1)
  def scalaDescriptor: _root_.scalapb.descriptors.EnumDescriptor = Semanticdb3Proto.scalaDescriptor.enums(1)
}