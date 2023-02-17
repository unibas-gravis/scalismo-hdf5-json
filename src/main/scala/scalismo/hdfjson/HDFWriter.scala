package scalismo.hdfjson

import java.io.PrintWriter
import scala.util.Try

object HDFWriter {

  def writeHDFJson(file: HDFFile, jsonOutFile: java.io.File): Try[Unit] = Try {
    val s = upickle.default.write[HDFFile](file)

    val writer = PrintWriter(jsonOutFile)
    writer.println(s)
    writer.close()
  }
}
