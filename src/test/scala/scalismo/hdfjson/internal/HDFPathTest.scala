package scalismo.hdfjson.internal

import scalismo.hdfjson.HDFPath

class HDFPathTest extends munit.FunSuite {
  test("an empty path evaluates to /") {
    val path = HDFPath("")
    assertEquals(path.toString, "/")
  }

  test("a path with one element evaluates to /element") {
    val path = HDFPath("a")
    assertEquals(path.toString, "/a")
  }

  test("a path with multiple elements evaluates to /element1/element2/...") {
    val path = HDFPath("a") / "b" / "c"
    assertEquals(path.toString, "/a/b/c")
  }

  test("a path with an empty element evaluates correctly") {
    val path = HDFPath("a") / "" / "c"
    assertEquals(path.toString, "/a/c")
  }

  test("a path with an empty path elements results in the correct path") {
    val path = HDFPath("a") / "b" / ""
    assertEquals(path.toString, "/a/b")
  }

  test("a path with several components in it results in the correct path") {
    val path = HDFPath("a") / "/b/c" / "d"
    assertEquals(path.toString, "/a/b/c/d")
  }
}
