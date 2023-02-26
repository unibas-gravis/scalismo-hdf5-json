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

package scalismo.hdf5json

import scalismo.hdf5json.HDFPath

case class HDFPath(components: Seq[String]) {

  def /(newComponent: String): HDFPath = {
    HDFPath(this, newComponent)
  }

  def parent: HDFPath = {
    HDFPath(components.dropRight(1))
  }

  def lastComponent: String = {
    components.last
  }

  override def toString: String = {
    "/" + components.mkString("/")
  }
}
object HDFPath {
  def apply(path: String): HDFPath = {
    HDFPath(path.split("/").toSeq.filterNot(_.isEmpty))
  }
  def apply(path: HDFPath, newComponent: String): HDFPath = {
    HDFPath(
      path.components ++ newComponent.split("/").toSeq.filterNot(_.isEmpty)
    )
  }

  def apply(path: HDFPath, newComponent: HDFPath): HDFPath = {
    HDFPath(path.components ++ newComponent.components)
  }

  def root: HDFPath = HDFPath(Seq.empty)
}
