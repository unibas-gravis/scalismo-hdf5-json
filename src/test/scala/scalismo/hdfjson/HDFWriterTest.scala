package scalismo.hdfjson

import munit.FunSuite

class HDFWriterTest extends FunSuite {

  test("write a simple dataset") {
    val file = HDFIOTestCases.simpleHDFFile
    HDFWriter.writeHDFJson(file, new java.io.File("test.hdf5.json")).get
  }
}

object HDFIOTestCases {
  def simpleHDFFile: HDFFile = {

    val group11id = HDFIdentifier.randomUUID()
    val group11 = HDFGroup(Seq.empty, Seq.empty)

    val group1id = HDFIdentifier.randomUUID()
    val group1 = HDFGroup(
      Seq.empty,
      Seq(HDFLink("g11", Collection.groups, group11id, LinkType.H5L_TYPE_HARD))
    )

    val dataset1id = HDFIdentifier.randomUUID()
    val dataset = HDFDataset(
      HDFDataSpace.simple_dataspace(Array(3)),
      HDFDatatype.floating_point_datatype(
        FloatingPointDatatype.Base.H5T_IEEE_F32LE
      ),
      ujson.Arr(Seq(1f, 2f, 3f))
    )

    val rootGroupid = HDFIdentifier.randomUUID()
    val rootGroup = HDFGroup(
      Seq(
        HDFAttribute(
          "attr1",
          HDFDatatype.string_datatype(
            CharSet.H5T_CSET_UTF8,
            StrPad.H5T_STR_NULLTERM,
            Length.H5T_VARIABLE
          ),
          HDFDataSpace.simple_dataspace(Array(3)),
          ujson.Str("abc")
        )
      ),
      Seq(
        HDFLink("g1", Collection.groups, group1id, LinkType.H5L_TYPE_HARD),
        HDFLink("d1", Collection.datasets, dataset1id, LinkType.H5L_TYPE_HARD)
      )
    )

    HDFFile(
      rootGroupid,
      Map(rootGroupid -> rootGroup, group1id -> group1, group11id -> group11),
      Map(dataset1id -> dataset)
    )

  }
}
