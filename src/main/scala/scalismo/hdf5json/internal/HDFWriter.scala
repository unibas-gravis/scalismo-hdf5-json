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
package scalismo.hdf5json.internal

import scalismo.hdf5json.internal.HDFFile

import java.io.PrintWriter
import scala.util.Try

object HDFWriter {

  def writeHDF5Json(file: HDFFile, jsonOutFile: java.io.File): Try[Unit] = Try {
    val s = upickle.default.write[HDFFile](file)

    val writer = PrintWriter(jsonOutFile)
    writer.println(s)
    writer.close()
  }
}
