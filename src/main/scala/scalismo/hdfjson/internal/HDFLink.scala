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
package scalismo.hdfjson.internal

import scalismo.hdfjson.internal.{Collection, HDFIdentifier, HDFLink, LinkType}
import upickle.default.{ReadWriter, macroRW}
import upickle.implicits.key

enum Collection {
  case datasets
  case groups
  case datatypes
}

object Collection {
  given rw: ReadWriter[Collection] = macroRW
}

enum LinkType {
  case H5L_TYPE_HARD
}

object LinkType {
  given rw: ReadWriter[LinkType] = macroRW
}

final case class HDFLink(
    title: String,
    collection: Collection,
    id: HDFIdentifier,
    @key("class") clazz: LinkType
)
object HDFLink {
  given rwHL: ReadWriter[HDFLink] = macroRW
}
