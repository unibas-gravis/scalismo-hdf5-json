package scalismo.hdfjson.internal

import scalismo.hdfjson.internal.HDFUserblock
import upickle.default.*

case class HDFUserblock()
object HDFUserblock {
  given rw: ReadWriter[HDFUserblock] = macroRW
}
