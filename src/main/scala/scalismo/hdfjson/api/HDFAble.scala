package scalismo.hdfjson.api

import scalismo.hdfjson.Length.StringLength
import scalismo.hdfjson.{CharSet, HDFDataSpace, HDFDatatype, StrPad}

/** Typeclass for types that can be represented as a HDF5 object.
  */
trait HDFAble[A] {
  def datatype(value: A): HDFDatatype
  def dataspace(value: A): HDFDataSpace

  def toUjsonValue(value: A): ujson.Value
  def fromUjsonValue(value: ujson.Value): Option[A]
}

object HDFAble {
  given HDFAble[String] with {
    def datatype(value: String) = HDFDatatype.string_datatype(
      CharSet.H5T_CSET_UTF8,
      StrPad.H5T_STR_NULLPAD,
      StringLength(value.length)
    )
    def dataspace(value: String) = HDFDataSpace.scalar_dataspace

    def toUjsonValue(value: String): ujson.Value = ujson.Str(value)

    def fromUjsonValue(value: ujson.Value): Option[String] = value.strOpt

  }

}
