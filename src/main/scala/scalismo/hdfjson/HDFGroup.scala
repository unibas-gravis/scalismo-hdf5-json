package scalismo.hdfjson

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
