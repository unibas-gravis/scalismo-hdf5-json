package scalismo.hdfjson

import upickle.default.*

case class HDFFileDriverInfo()

object HDFFileDriverInfo {
  given rw: ReadWriter[HDFFileDriverInfo] = macroRW
}
