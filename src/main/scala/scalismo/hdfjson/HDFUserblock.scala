package scalismo.hdfjson

import upickle.default.*

case class HDFUserblock()
object HDFUserblock {
  given rw: ReadWriter[HDFUserblock] = macroRW
}
