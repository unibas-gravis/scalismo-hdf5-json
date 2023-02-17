package scalismo.hdfjson

import upickle.default.{ReadWriter, macroRW}
import upickle.implicits.key

enum Collection {
  case datasets
  case groups
  case datatypes
}

object Collection {
  given rw: ReadWriter[Collection] = macroRW
}

enum LinkType {
  case H5L_TYPE_HARD
}

object LinkType {
  given rw: ReadWriter[LinkType] = macroRW
}

final case class HDFLink(
    title: String,
    collection: Collection,
    id: HDFIdentifier,
    @key("class") clazz: LinkType
)
object HDFLink {
  given rwHL: ReadWriter[HDFLink] = macroRW
}
