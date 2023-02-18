package scalismo.hdfjson.internal

import scalismo.hdfjson.internal.{DatasetCreationProperties, HDFDataSpace, HDFDataset, HDFDatatype}
import upickle.default.*
import upickle.implicits.key

case class DatasetCreationProperties() {}
object DatasetCreationProperties {
  given rw: ReadWriter[DatasetCreationProperties] = upickle.default
    .readwriter[ujson.Value]
    .bimap[DatasetCreationProperties](
      x => ujson.Obj(),
      x => DatasetCreationProperties()
    )
}

case class HDFDataset(
    shape: HDFDataSpace,
    @key("type") dtype: HDFDatatype,
    value: ujson.Value,
    alias: Seq[String] = Seq.empty,
    creationProperties: DatasetCreationProperties = DatasetCreationProperties()
)
object HDFDataset {

  // def apply(shape : HDFDataSpace, @key("type") dtype : HDFDatatype, value : ujson.Value) : HDFDataset = HDFDataset(shape, dtype, value, None, None)

  given rw: ReadWriter[HDFDataset] = macroRW
}
