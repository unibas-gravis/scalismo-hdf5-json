package example

import scalismo.hdfjson.{FloatArray1D, FloatArray2D, HDFJson, HDFPath}

object SimpleHDFJsonExample {

  def main(args : Array[String]) : Unit = {
    val hdfJson = HDFJson
      .createEmpty
      .addGroup(HDFPath("/group1/group2"))
      .addAttribute[String](HDFPath("/group1/group2"), "attribute1", "value1")
      .addDataset[FloatArray1D](HDFPath("/group1/group2"), "dataset1", FloatArray1D.from(Array(1.0f, 2.0f, 3.0f)))
      .addDataset[FloatArray2D](HDFPath("/group1/group2"), "dataset2", FloatArray2D.from(Array(Array(1.0f, 2.0f), Array(3.0f, 4.0f))))

    HDFJson.writeToFile(hdfJson, java.io.File("example.h5.json")).get
  }

}
