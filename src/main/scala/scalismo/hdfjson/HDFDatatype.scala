package scalismo.hdfjson

import upickle.default.*
import upickle.implicits.key

enum DataClass {
  case H5T_STRING
  case H5T_INTEGER
  case H5T_FLOAT
}
object DataClass {
  given rw: ReadWriter[DataClass] = macroRW
}

enum CharSet {
  case H5T_CSET_ASCII
  case H5T_CSET_UTF8
}
object CharSet {
  given rw: ReadWriter[CharSet] = macroRW
}

enum StrPad {
  case H5T_STR_NULLPAD
  case H5T_STR_NULLTERM
  case H5T_STR_SPACEPAD
}

object StrPad {
  given rw: ReadWriter[StrPad] = macroRW
}

enum Length {
  case H5T_VARIABLE
  case StringLength(length: Int)
}

object Length {
  given rw: ReadWriter[Length] = upickle.default
    .readwriter[ujson.Value]
    .bimap[Length](
      x =>
        x match {
          case H5T_VARIABLE    => ujson.Str("H5T_VARIABLE")
          case StringLength(l) => ujson.Value.JsonableInt(l)
        },
      value => {
        value.strOpt match {
          case Some("H5T_VARIABLE") => H5T_VARIABLE
          case None                 => StringLength(value.num.toInt)
        }
      }
    )
}

enum HDFDatatype(@key("class") val clazz: DataClass) {
  case string_datatype(charSet: CharSet, strPad: StrPad, length: Length)
      extends HDFDatatype(DataClass.H5T_STRING)
  case floating_point_datatype(base: FloatingPointDatatype.Base)
      extends HDFDatatype(DataClass.H5T_FLOAT)
  case integer_datatype(base: IntegerDatatype.Base)
      extends HDFDatatype(DataClass.H5T_INTEGER)
}

object FloatingPointDatatype {
  enum Base {
    case H5T_IEEE_F32BE
    case H5T_IEEE_F32LE
    case H5T_IEEE_F64BE
    case H5T_IEEE_F64LE
  }

  object Base {
    given rw: ReadWriter[Base] = macroRW
  }
}

object IntegerDatatype {
  enum Base {
    case H5T_STD_I8BE
    case H5T_STD_I8LE
    case H5T_STD_I16BE
    case H5T_STD_I16LE
    case H5T_STD_I32BE
    case H5T_STD_I32LE
    case H5T_STD_I64BE
    case H5T_STD_I64LE
    case H5T_STD_U8BE
    case H5T_STD_U8LE
    case H5T_STD_U16BE
    case H5T_STD_U16LE
    case H5T_STD_U32BE
    case H5T_STD_U32LE
    case H5T_STD_U64BE
    case H5T_STD_U64LE
  }

  object Base {
    given rw: ReadWriter[Base] = macroRW
  }
}

object HDFDatatype {
  // as ujson does not seem to be able to serialize enums correctly
  given rw: ReadWriter[HDFDatatype] = upickle.default
    .readwriter[ujson.Value]
    .bimap[HDFDatatype](
      x =>
        x match {
          case HDFDatatype.string_datatype(charSet, strPad, length) =>
            ujson.Obj(
              "class" -> writeJs(x.clazz),
              "charSet" -> writeJs(charSet),
              "strPad" -> writeJs(strPad),
              "length" -> writeJs(length)
            )
          case HDFDatatype.floating_point_datatype(base) =>
            ujson.Obj(
              "class" -> writeJs(x.clazz),
              "base" -> writeJs(base)
            )
        },
      value => {
        read[DataClass](value("class")) match {
          case DataClass.H5T_FLOAT =>
            HDFDatatype.floating_point_datatype(
              read(value("base"))
            )
          case DataClass.H5T_INTEGER =>
            HDFDatatype.integer_datatype(
              read(value("base"))
            )
          case DataClass.H5T_STRING => {
            HDFDatatype.string_datatype(
              read[CharSet](value("charSet")),
              read[StrPad](value("strPad")),
              read[Length](value("length"))
            )
          }
        }
      }
    )
}
