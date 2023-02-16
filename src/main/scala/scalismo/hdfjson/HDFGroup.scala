package scalismo.hdfjson

import upickle.default.*

import java.io.File



case class HDFGroup(attributes : Seq[HDFAttribute], links : Seq[HDFLink])

object HDFGroup {
  given  rw : ReadWriter[HDFGroup] = macroRW
}