package scalismo.hdfjson.internal

import scalismo.hdfjson.internal.HDFIdentifier
import upickle.default.*

case class HDFIdentifier(value: String)

object HDFIdentifier {

  def randomUUID(): HDFIdentifier = HDFIdentifier(
    java.util.UUID.randomUUID().toString
  )

  given rw: ReadWriter[HDFIdentifier] = stringKeyRW(
    readwriter[String].bimap(_.value, HDFIdentifier(_))
  )
}
