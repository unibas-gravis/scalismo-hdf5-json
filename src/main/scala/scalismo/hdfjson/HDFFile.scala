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

package scalismo.hdfjson

import upickle.default.*
import java.io.File
import scala.util.Try

case class HDFFile(
    root: HDFIdentifier,
    groups: Map[HDFIdentifier, HDFGroup],
    datasets: Map[HDFIdentifier, HDFDataset],
    apiVersion: String
)

object HDFFile {
  def apply(
      root: HDFIdentifier,
      groups: Map[HDFIdentifier, HDFGroup],
      datasets: Map[HDFIdentifier, HDFDataset]
  ): HDFFile = new HDFFile(root, groups, datasets, apiVersion = "1.0.0")

  def empty: HDFFile = {
    val rootGid = HDFIdentifier.randomUUID()
    val root = HDFGroup()
    HDFFile(rootGid, Map(rootGid -> root), Map.empty)
  }

  def fromFile(file: File): Try[HDFFile] = {
    HDFReader.readHDFJsonFile(file)
  }

  given rw: ReadWriter[HDFFile] = macroRW

}
