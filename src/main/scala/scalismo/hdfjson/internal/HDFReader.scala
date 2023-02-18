package scalismo.hdfjson.internal

import scalismo.hdfjson.internal.HDFFile
import upickle.default.*

import java.io.File
import scala.io.Source
import scala.util.Try

object HDFReader {

  def readHDFJsonFile(file: File): Try[HDFFile] = Try {
    val jsonString = Source.fromFile(file).getLines().mkString("\n")
    read[HDFFile](jsonString)

  }
}
