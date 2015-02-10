import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

abstract class StackTable[T] {

  val file: File

  def getDate(n: scala.xml.NodeSeq): Long = n.text match {
    case "" => 0
    case s  => parseDate(s)
  }

  def parseDate(s: String): Long = LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toInstant(ZoneOffset.UTC).getEpochSecond

  def getInt(n: scala.xml.NodeSeq): Int = n.text match {
    case "" => 0
    case x  => x.toInt
  }

  def parseXml(x: scala.xml.Elem): T

  def parse(s: String): Option[T] =
    if (s.startsWith("  <row ")) Some(parseXml(scala.xml.XML.loadString(s)))
    else None

}
