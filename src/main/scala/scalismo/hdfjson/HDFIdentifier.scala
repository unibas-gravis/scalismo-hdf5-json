package scalismo.hdfjson

import upickle.default.*

case class HDFIdentifier(value : String)

object HDFIdentifier {

  def randomUUID() : HDFIdentifier = HDFIdentifier(java.util.UUID.randomUUID().toString)

  given  rw : ReadWriter[HDFIdentifier] = stringKeyRW(readwriter[String].bimap(_.value, HDFIdentifier(_)))
}

