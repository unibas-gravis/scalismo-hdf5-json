package scalismo.hdfjson

import java.io.File
import java.net.URL;


class HDFReaderTest extends munit.FunSuite {
  test("a typical hdf5 json file can be successfully parsed") {
    val hdfFile = HDFIOTestCases.simpleHDFFile
    HDFWriter.writeHDFJson(hdfFile, new File("test.hdf5.json")).get
    val readHDFFile = HDFReader.readHDFJsonFile(new File("test.hdf5.json")).get
    assertEquals(readHDFFile, hdfFile)

  }
}