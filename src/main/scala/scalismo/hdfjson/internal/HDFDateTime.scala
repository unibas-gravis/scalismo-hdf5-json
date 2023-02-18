package scalismo.hdfjson.internal

import scalismo.hdfjson.internal.HDFDateTime
import upickle.default.*

case class HDFDateTime(time: Long)
object HDFDateTime {
  def now(): HDFDateTime = HDFDateTime(System.currentTimeMillis())

  given rw: ReadWriter[HDFDateTime] = macroRW
}
