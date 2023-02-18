package scalismo.hdfjson

import scalismo.hdfjson.HDFJson
import scalismo.hdfjson.internal.*
import scalismo.hdfjson.internal.Length.StringLength
import upickle.default.read

import scala.collection.mutable
import scala.util.Try

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
        pathToDatsetMap += (pathSoFar -> (link.id, hdfFile.datasets(link.id)))
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

  def addAttribute[A](path: HDFPath, attributeName: String, attributeValue: A)(
      using hdfConverter: HDFAble[A]
  ): HDFJson = {
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

  def addDataset[A](path: HDFPath, datasetName: String, datasetValue: A)(using
      hdfConverter: HDFAble[A]
  ): HDFJson = {
    val (gid, group) = pathToGroupMap(path)
    val dataset = HDFDataset(
      hdfConverter.dataspace(datasetValue),
      hdfConverter.datatype(datasetValue),
      hdfConverter.toUjsonValue(datasetValue),
      alias=Seq((path / datasetName).toString)
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

  def getDataset[A](path: HDFPath, datasetName: String)(using
      hdfConverter: HDFAble[A]
  ): Option[A] = {
    if (pathToDatsetMap.contains(path)) {
      val (did, dataset) = pathToDatsetMap(path)
      hdfConverter.fromUjsonValue(dataset.value)
    } else {
      None
    }
  }

  def toJson : String = {
    upickle.default.write[HDFFile](hdfFile)
  }
}

object HDFJson {
  def readFromFile(jsonFile: java.io.File): Try[HDFJson] = {
    HDFReader
      .readHDFJsonFile(jsonFile)
      .map(f => HDFJson(f))
  }

  def fromJson(jsonString: String): Try[HDFJson] = Try {
    HDFJson(read[HDFFile](jsonString))
  }

  def writeToFile(HDFJson: HDFJson, outputFile : java.io.File) : Try[Unit] = {
    HDFWriter.writeHDFJson(HDFJson.hdfFile, outputFile)
  }

  def createEmpty: HDFJson = {
    val hdffile = HDFFile.empty
    new HDFJson(hdffile)
  }
}
