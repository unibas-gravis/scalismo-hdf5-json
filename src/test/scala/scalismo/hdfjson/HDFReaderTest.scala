package scalismo.hdfjson

import java.io.File
import java.net.URL
import scala.util.{Try, Success, Failure}


class HDFReaderTest extends munit.FunSuite {
  test("a typical hdf5 json file can be successfully parsed") {
    val resource: URL = getClass.getResource("sampleFile.json")
    HDFReader.readHDFJsonFile(new File(resource.getFile)) match {
      case Success(_) =>
        assert(true)
      case Failure(e) =>
        println(e.getMessage)
        fail(e.getMessage)
    }

  }
}