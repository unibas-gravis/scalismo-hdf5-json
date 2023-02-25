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

import scalismo.hdfjson.HDFJson
import scalismo.hdfjson.internal.*
import scalismo.hdfjson.internal.Length.StringLength
import upickle.default.read

import scala.collection.mutable
import scala.util.Try

/** API for defining hdf5 files in a json format. The HDFJson class is
  * immutable. Every operation on a HDFJson object returns a new HDFJson object.
  */
class HDFJson(private val hdfFile: HDFFile) {

  // we build an index structure which contains all the path with links to the group or datasets
  private val pathToGroupMap: mutable.Map[HDFPath, (HDFIdentifier, HDFGroup)] =
    mutable.Map.empty
  private val pathToDatsetMap
      : mutable.Map[HDFPath, (HDFIdentifier, HDFDataset)] = mutable.Map.empty

  buildIndex()

  private def buildIndex(): Unit = {
    val rootId = hdfFile.root
    val rootGroup = hdfFile.groups(rootId)
    buildIndex(HDFPath.root, rootId, rootGroup)

  }

  private def buildIndex(
      pathSoFar: HDFPath,
      groupId: HDFIdentifier,
      group: HDFGroup
  ): Unit = {
    pathToGroupMap += (pathSoFar -> (groupId, group))

    // add datasets from that group
    group.links
      .filter(link =>
        hdfFile.datasets.contains(link.id)
      ) // consider only dataset links
      .foreach { link =>
        val dsPath = pathSoFar / link.title
        pathToDatsetMap += (dsPath -> (link.id, hdfFile.datasets(link.id)))
      }

    // add groups and recurse
    group.links
      .filter(link =>
        hdfFile.groups.contains(link.id)
      ) // consider only links to groups
      .foreach { link =>
        val newPath = pathSoFar / link.title
        buildIndex(newPath, link.id, hdfFile.groups(link.id))
      }

  }

  def exists(path: HDFPath): Boolean =
    pathToGroupMap.contains(path) || pathToDatsetMap.contains(path)

  /** returns the path to all the groups defined in the file
    */
  def groups: Seq[HDFPath] = pathToGroupMap.keys.toSeq

  /** add a group with the given path. The group that is created is empty. All
    * the subgroups on the path are created. If the group already exists,
    * nothing is done.
    */
  def addGroup(path: HDFPath): HDFJson = {
    if (pathToGroupMap.contains(path)) {
      HDFJson(hdfFile) // nothing to do
    } else {
      val hdfJsonParentDir = addGroup(path.parent)

      // create the new group
      val gid = HDFIdentifier.randomUUID()
      val newGroup = HDFGroup(alias = Seq(path.toString))

      // find parent group
      val (parentGid, parentGroup) =
        hdfJsonParentDir.pathToGroupMap(path.parent)

      val parentGroupWithNewLink = parentGroup.copy(links =
        parentGroup.links :+ (HDFLink(
          path.lastComponent,
          Collection.groups,
          gid,
          LinkType.H5L_TYPE_HARD
        ))
      )

      val newGroups = hdfJsonParentDir.hdfFile.groups
        .updated(parentGid, parentGroupWithNewLink) + (gid -> newGroup)

      HDFJson(hdfJsonParentDir.hdfFile.copy(groups = newGroups))
    }
  }

  /** adds an attribute to the group at the given path. If the group with the
    * given path does not exist, it is created.
    *
    * @param path
    *   the path to the group
    * @attributeName
    *   the name of the attribute
    * @attributeValue
    *   the value of the attribute
    * @return
    *   a new HDFJson object with the attribute added
    */
  def addAttribute[A](path: HDFPath, attributeName: String, attributeValue: A)(
      using hdfConverter: HDFAble[A]
  ): HDFJson = {
    if (!pathToGroupMap.contains(path)) {
      addGroup(path).addAttribute(path, attributeName, attributeValue)
    } else {
      val (gid, group) = pathToGroupMap(path)
      val attribute = HDFAttribute(
        attributeName,
        hdfConverter.datatype(attributeValue),
        hdfConverter.dataspace(attributeValue),
        hdfConverter.toUjsonValue(attributeValue)
      )
      val newGroup = group.copy(attributes = group.attributes :+ attribute)
      HDFJson(hdfFile.copy(groups = hdfFile.groups.updated(gid, newGroup)))
    }
  }

  /** Tries to read the attribute with the given name from the group at the
    * given path.
    */
  def getAttribute[A](path: HDFPath, attributeName: String)(using
      hdfConverter: HDFAble[A]
  ): Option[A] = {

    if (pathToGroupMap.contains(path)) {
      val (gid, group) = pathToGroupMap(path)
      group.attributes
        .find(_.name == attributeName)
        .flatMap(attr => hdfConverter.fromUjsonValue(attr.value))
    } else {
      None
    }
  }

  /** adds a dataset to the group at the given path. If the path does not exist,
    * it is created.
    * @param path
    *   the path to the group
    * @param datasetName
    *   the name of the dataset
    * @param datasetValue
    *   the value of the dataset
    * @tparam A
    *   The type of the dataset
    * @return
    *   a new HDFJson object with the dataset added
    */
  def addDataset[A](path: HDFPath, datasetName: String, datasetValue: A)(using
      hdfConverter: HDFAble[A]
  ): HDFJson = {

    if (!pathToGroupMap.contains(path)) {
      addGroup(path).addDataset(path, datasetName, datasetValue)
    } else {
      val (gid, group) = pathToGroupMap(path)
      val dataset = HDFDataset(
        hdfConverter.dataspace(datasetValue),
        hdfConverter.datatype(datasetValue),
        hdfConverter.toUjsonValue(datasetValue),
        alias = Seq((path / datasetName).toString)
      )
      val datasetId = HDFIdentifier.randomUUID()
      val newGroup = group.copy(links =
        group.links :+ (HDFLink(
          datasetName,
          Collection.datasets,
          datasetId,
          LinkType.H5L_TYPE_HARD
        ))
      )
      HDFJson(
        hdfFile.copy(
          groups = hdfFile.groups.updated(gid, newGroup),
          datasets = hdfFile.datasets + (datasetId -> dataset)
        )
      )
    }
  }

  /** Tries to read the dataset with the given name from the group at the given
    * path.
    */
  def getDataset[A](path: HDFPath, datasetName: String)(using
      hdfConverter: HDFAble[A]
  ): Option[A] = {
    val fullPath = path / datasetName
    if (pathToDatsetMap.contains(fullPath)) {
      val (did, dataset) = pathToDatsetMap(fullPath)
      hdfConverter.fromUjsonValue(dataset.value)
    } else {
      None
    }
  }

  /** returns a json string representing the created structure
    */
  def toJson: String = {
    upickle.default.write[HDFFile](hdfFile)
  }
}

object HDFJson {

  /** create a HDFJson object from a given hdf.json file
    * @param jsonFile
    *   the hdf.json file
    * @return
    *   the HDFJson object
    */
  def readFromFile(jsonFile: java.io.File): Try[HDFJson] = {
    HDFReader
      .readHDFJsonFile(jsonFile)
      .map(f => HDFJson(f))
  }

  /** tries to create a HDFJson object from the given json string
    *
    * @param jsonString
    *   the json string
    * @return
    *   the HDFJson object
    */
  def fromJson(jsonString: String): Try[HDFJson] = Try {
    HDFJson(read[HDFFile](jsonString))
  }

  /** writes the given HDFJson object to the given file
    * @param HDFJson
    *   the HDFJson object
    * @param outputFile
    *   the output file
    */
  def writeToFile(HDFJson: HDFJson, outputFile: java.io.File): Try[Unit] = {
    HDFWriter.writeHDFJson(HDFJson.hdfFile, outputFile)
  }

  /** creates an empty HDFJson object
    */
  def createEmpty: HDFJson = {
    val hdffile = HDFFile.empty
    new HDFJson(hdffile)
  }
}
