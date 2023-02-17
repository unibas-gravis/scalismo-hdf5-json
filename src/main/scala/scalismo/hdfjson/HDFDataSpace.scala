package scalismo.hdfjson

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
