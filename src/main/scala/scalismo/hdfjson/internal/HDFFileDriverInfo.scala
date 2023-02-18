package scalismo.hdfjson.internal

import scalismo.hdfjson.internal.HDFFileDriverInfo
import upickle.default.*

case class HDFFileDriverInfo()

object HDFFileDriverInfo {
  given rw: ReadWriter[HDFFileDriverInfo] = macroRW
}
