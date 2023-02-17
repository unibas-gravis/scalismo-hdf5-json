package scalismo.hdfjson.api

import munit.FunSuite
import scalismo.hdfjson.{HDFIOTestCases, HDFPath}

class HDFJsonTest extends FunSuite {
  test("HDFJson should be able to retrieve a group") {
    val hdfFile = HDFIOTestCases.simpleHDFFile
    val hdfJsonAPI = HDFJson(hdfFile)
    assertEquals(hdfJsonAPI.groups.length, 3)
    assert(
      hdfJsonAPI.groups.map(_.toString).toSet == Set("/", "/g1", "/g1/g11")
    )
  }

  test("HDFJson should be able to  add a new group to an empty file") {
    val hdfj = HDFJson.createEmpty.addGroup(HDFPath("/g1"))
    assertEquals(hdfj.groups.length, 2)
    assertEquals(hdfj.exists(HDFPath("/g1")), true)
    assert(hdfj.groups.map(_.toString).toSet == Set("/", "/g1"))
  }

  test(
    "HDFJson should be able to  add a new group with several non-existant path"
  ) {
    val hdfj = HDFJson.createEmpty.addGroup(HDFPath("/g1/g11/g111"))
    assertEquals(hdfj.groups.length, 4)
    assertEquals(hdfj.exists(HDFPath("/g1/g11/g111")), true)
    assert(
      hdfj.groups.map(_.toString).toSet == Set(
        "/",
        "/g1",
        "/g1/g11",
        "/g1/g11/g111"
      )
    )
  }

  test("HDFJson should be able to write and retrieve a string attribute") {
    val hdfj = HDFJson.createEmpty
      .addGroup(HDFPath("/g1/g11/g111"))
      .addAttribute(HDFPath("/g1/g11/g111"), "attr1", "value1")
      .addAttribute(HDFPath("/g1/g11/g111"), "attr2", "value2")
    assertEquals(
      hdfj
        .getAttribute[String](HDFPath("/g1/g11/g111"), "attr1")
        .getOrElse("not-retrieved"),
      "value1"
    )
    assertEquals(
      hdfj
        .getAttribute[String](HDFPath("/g1/g11/g111"), "attr2")
        .getOrElse("not-retrieved"),
      "value2"
    )

  }

  test("HDFJson should be able to write and retrieve a string dataset") {
    val hdfj = HDFJson.createEmpty
      .addGroup(HDFPath("/g1/g11/g111"))
      .addDataset(HDFPath("/g1/g11"), "ds1", "value1")
      .addDataset(HDFPath("/g1/g11/g111"), "ds2", "value2")

    assertEquals(
      hdfj
        .getDataset[String](HDFPath("/g1/g11"), "ds1")
        .getOrElse("not-retrieved"),
      "value1"
    )
    assertEquals(
      hdfj
        .getDataset[String](HDFPath("/g1/g11/g111"), "ds2")
        .getOrElse("not-retrieved"),
      "value2"
    )

  }

}
