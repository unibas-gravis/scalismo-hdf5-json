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
package scalismo.hdf5json.internal

import scalismo.hdf5json.internal.{HDFDataSpace, ShapeClass}
import upickle.default.*
import upickle.implicits.key

enum ShapeClass {
  case H5S_SCALAR
  case H5S_SIMPLE
}

object ShapeClass {
  given rw: ReadWriter[ShapeClass] = macroRW
}
enum HDFDataSpace(@key("class") val clazz: ShapeClass) {
  case scalar_dataspace extends HDFDataSpace(ShapeClass.H5S_SCALAR)
  case simple_dataspace(@key("dims") dims: Seq[Int])
      extends HDFDataSpace(ShapeClass.H5S_SIMPLE)
}

object HDFDataSpace {
  given rw: ReadWriter[HDFDataSpace] = upickle.default
    .readwriter[ujson.Value]
    .bimap[HDFDataSpace](
      x =>
        x match {
          case ds @ HDFDataSpace.scalar_dataspace =>
            ujson.Obj(
              "class" -> write(ds.clazz).replace("\"", "")
            )
          case ds @ HDFDataSpace.simple_dataspace(dims) =>
            ujson.Obj(
              "class" -> writeJs(ds.clazz),
              "dims" -> writeJs(dims)
            )
        },
      value =>
        read[ShapeClass](value("class")) match
          case ShapeClass.H5S_SCALAR => HDFDataSpace.scalar_dataspace
          case ShapeClass.H5S_SIMPLE =>
            HDFDataSpace.simple_dataspace(read[Array[Int]](value("dims")))
    )
}
