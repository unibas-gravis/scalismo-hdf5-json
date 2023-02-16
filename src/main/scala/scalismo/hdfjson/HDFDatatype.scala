package scalismo.hdfjson

import upickle.default.*
import upickle.implicits.key

enum DataClass  {
  case H5T_STRING
  case H5T_INTEGER
  case H5T_FLOAT
}
object DataClass {
  given rw : ReadWriter[DataClass] = macroRW
}

enum CharSet {
  case H5T_CSET_ASCII
  case H5T_CSET_UTF8
}
object CharSet {
  given rw : ReadWriter[CharSet] = macroRW
}

enum StrPad {
  case H5T_STR_NULLPAD
  case H5T_STR_NULLTERM
  case H5T_STR_SPACEPAD
}

object StrPad {
  given rw : ReadWriter[StrPad] = macroRW
}

enum Length {
  case H5T_VARIABLE
}

object Length {
  given rw : ReadWriter[Length] = macroRW
}

enum Base {
  case H5T_IEEE_F32BE
  case H5T_IEEE_F32LE
  case H5T_IEEE_F64BE
  case H5T_IEEE_F64LE
}

object Base {
  given rw: ReadWriter[Base] = macroRW
}

enum HDFDatatype(@key("class") val clazz: DataClass) {
  case string_datatype(charSet: CharSet, strPad: StrPad, length: Length) extends HDFDatatype(DataClass.H5T_STRING)
  case floating_point_datatype(base : Base) extends HDFDatatype(DataClass.H5T_FLOAT)
}
object HDFDatatype {
  // as ujson does not seem to be able to serialize enums correctly
  given rw : ReadWriter[HDFDatatype] = upickle.default.readwriter[ujson.Value].bimap[HDFDatatype](
      x => x match {
        case HDFDatatype.string_datatype(charSet, strPad, length) => ujson.Obj(
          "class" -> writeJs(x.clazz),
          "charSet" -> writeJs(charSet),
          "strPad" -> writeJs(strPad),
          "length" -> writeJs(length)
        )
        case HDFDatatype.floating_point_datatype(base) => ujson.Obj(
          "class" -> writeJs(x.clazz),
          "base" -> writeJs(base)
        )
      },
    value => read[DataClass](value("class")) match {
      case DataClass.H5T_FLOAT => HDFDatatype.floating_point_datatype(
        read(value("base"))
      )
      case DataClass.H5T_STRING => HDFDatatype.string_datatype(
        read(value("charSet")),
        read(value("strPad")),
        read(value("length"))
      )
    }
  )
}


