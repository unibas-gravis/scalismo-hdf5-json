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

import scalismo.hdf5json.*
import upickle.default.*

import java.io.File

case class GroupCreationProperties() {}
object GroupCreationProperties {
  given rw: ReadWriter[GroupCreationProperties] = upickle.default
    .readwriter[ujson.Value]
    .bimap[GroupCreationProperties](
      x => ujson.Obj(),
      x => GroupCreationProperties()
    )
}

case class HDFGroup(
    attributes: Seq[HDFAttribute] = Seq.empty,
    links: Seq[HDFLink] = Seq.empty,
    alias: Seq[String] = Seq.empty,
    created: HDFDateTime = HDFDateTime.now(),
    lastModified: HDFDateTime = HDFDateTime.now(),
    creationProperties: GroupCreationProperties = GroupCreationProperties()
)

object HDFGroup {
  // def apply(attributes : Seq[HDFAttribute], links : Seq[HDFLink]) : HDFGroup = HDFGroup(attributes, links, None, None, None, None)

  given rw: ReadWriter[HDFGroup] = macroRW
}
