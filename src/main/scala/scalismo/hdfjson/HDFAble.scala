package scalismo.hdfjson

import scalismo.hdfjson.internal.FloatingPointDatatype.Base
import scalismo.hdfjson.internal.Length.StringLength
import scalismo.hdfjson.internal.{CharSet, FloatingPointDatatype, HDFDataSpace, HDFDatatype, IntegerDatatype, Length, StrPad}

import scala.reflect.ClassTag

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
      Length.H5T_VARIABLE
    )
    def dataspace(value: String) = HDFDataSpace.scalar_dataspace

    def toUjsonValue(value: String): ujson.Value = ujson.Str(value)

    def fromUjsonValue(value: ujson.Value): Option[String] = value.strOpt

  }

  given HDFAble[Float] with {
    def datatype(value: Float) = HDFDatatype.floating_point_datatype(FloatingPointDatatype.Base.H5T_IEEE_F32LE)

    def dataspace(value: Float) = HDFDataSpace.scalar_dataspace

    def toUjsonValue(value: Float): ujson.Value = ujson.Num(value)

    def fromUjsonValue(value: ujson.Value): Option[Float] = value.numOpt.map(_.toFloat)

  }

  given HDFAble[Int] with {
    def datatype(value: Int) = HDFDatatype.integer_datatype(IntegerDatatype.Base.H5T_STD_I32LE)

    def dataspace(value: Int) = HDFDataSpace.scalar_dataspace

    def toUjsonValue(value: Int): ujson.Value = ujson.Num(value)

    def fromUjsonValue(value: ujson.Value): Option[Int] = value.numOpt.map(_.toInt)

  }


  given HDFAble[FloatArray1D] with {
    def datatype(value: FloatArray1D) = HDFDatatype.floating_point_datatype(FloatingPointDatatype.Base.H5T_IEEE_F32LE)

    def dataspace(value: FloatArray1D) = HDFDataSpace.simple_dataspace(Seq(value.toArray.size))

    def toUjsonValue(floatArray: FloatArray1D): ujson.Value = ujson.Arr.from(floatArray.toArray)

    def fromUjsonValue(value: ujson.Value): Option[FloatArray1D] = value.arrOpt
      .map(arr => FloatArray1D.from(arr.toArray.map(_.num.toFloat)))

  }




  given  HDFAble[IntArray1D]  with {
    def datatype(value: IntArray1D) = HDFDatatype.integer_datatype(IntegerDatatype.Base.H5T_STD_I32LE)

    def dataspace(value: IntArray1D) = HDFDataSpace.simple_dataspace(Seq(value.toArray.size))

    def toUjsonValue(value: IntArray1D): ujson.Value = ujson.Arr.from(value.toArray)

    def fromUjsonValue(value: ujson.Value): Option[IntArray1D] = value.arrOpt
      .map(arr => IntArray1D.from(arr.toArray.map(_.num.toInt)))

  }


  given HDFAble[FloatArray2D] with {
    def datatype(value: FloatArray2D) = HDFDatatype.floating_point_datatype(FloatingPointDatatype.Base.H5T_IEEE_F32LE)

    def dataspace(value: FloatArray2D) = {
      val outerArray: Array[Array[Float]] = value.toArray
      HDFDataSpace.simple_dataspace(Seq(outerArray(0).size, outerArray.size))
    }

    def toUjsonValue(floatArray2D: FloatArray2D): ujson.Value = {
      val outer = floatArray2D.toArray
      ujson.Arr.from(outer.map((inner: Array[Float]) => ujson.Arr.from(inner)))
    }

    def fromUjsonValue(value: ujson.Value): Option[FloatArray2D] = {
      val twodarrayOpt: Option[Array[Array[Float]]] = value.arrOpt
        .map(outerArr => outerArr.toArray
          .flatMap(value => value.arrOpt
            .map(innerArr => innerArr.toArray.map(_.num.toFloat))))

      twodarrayOpt.map(twodarray => FloatArray2D.from(twodarray))
    }
  }

}



opaque type FloatArray1D = Array[Float]

object FloatArray1D {
  def from(array: Array[Float]): FloatArray1D = array

  extension (x: FloatArray1D)
    def toArray: Array[Float] = x

}

opaque type FloatArray2D = Array[Array[Float]]
object FloatArray2D {
  def from(array: Array[Array[Float]]): FloatArray2D = array

  extension (x: FloatArray2D)
    def toArray: Array[Array[Float]] = x

}

opaque type IntArray1D = Array[Int]

object IntArray1D {
  def from(array: Array[Int]): IntArray1D = array

  extension (x: IntArray1D) {
    def toArray: Array[Int] = x


  }
}
