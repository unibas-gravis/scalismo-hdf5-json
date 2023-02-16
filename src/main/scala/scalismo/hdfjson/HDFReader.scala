package scalismo.hdfjson

import java.io.File
import scala.util.Try
import upickle.default.*

import scala.io.Source

object HDFReader {

  def readHDFJsonFile(file : File) : Try[HDFFile] = Try {
    val jsonString = Source.fromFile(file).getLines().mkString("\n")
    read[HDFFile](jsonString)

  }
}
