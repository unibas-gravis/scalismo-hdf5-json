/*
 * Copyright 2023 University of Basel, Graphics and Vision Research Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package example

import scalismo.hdf5json.{FloatArray1D, FloatArray2D, HDF5Json, HDFPath}

/** Example of how to create a HDFJson object with groups, datasets and
  * attributes and write it to a file.
  */
object SimpleHDFJsonExample {

  def main(args: Array[String]): Unit = {
    val hdfJson = HDF5Json.createEmpty
      .addGroup(HDFPath("/group1/group2"))
      .addAttribute[String](HDFPath("/group1/group2"), "attribute1", "value1")
      .addDataset[FloatArray1D](
        HDFPath("/group1/group2"),
        "dataset1",
        FloatArray1D.from(Array(1.0f, 2.0f, 3.0f))
      )
      .addDataset[FloatArray2D](
        HDFPath("/group1/group2"),
        "dataset2",
        FloatArray2D.from(Array(Array(1.0f, 2.0f), Array(3.0f, 4.0f)))
      )

    HDF5Json.writeToFile(hdfJson, java.io.File("example.h5.json")).get
  }

}
