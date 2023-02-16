package scalismo.hdfjson

import upickle.default.*
import upickle.implicits.key

case class HDFDataset(shape : HDFDataSpace, @key("type") dtype : HDFDatatype, value : ujson.Value)
object HDFDataset {
  given rw : ReadWriter[HDFDataset] = macroRW
}
