/*
 * Copyright 2023 University of Basel, Graphics and Vision Research Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scalismo.hdfjson.internal

import scalismo.hdfjson.internal.{
  DatasetCreationProperties,
  HDFDataSpace,
  HDFDataset,
  HDFDatatype
}
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
