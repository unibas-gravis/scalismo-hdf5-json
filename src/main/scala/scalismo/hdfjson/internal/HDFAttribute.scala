package scalismo.hdfjson.internal

import scalismo.hdfjson.internal.{HDFAttribute, HDFDataSpace, HDFDatatype}
import upickle.default.{ReadWriter, macroRW}
import upickle.implicits.key
case class HDFAttribute(
    name: String,
    @key("type") dtype: HDFDatatype,
    shape: HDFDataSpace,
    value: ujson.Value
)

object HDFAttribute {
  given rw: ReadWriter[HDFAttribute] = macroRW
}
