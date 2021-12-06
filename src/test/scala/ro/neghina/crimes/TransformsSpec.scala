package ro.neghina.crimes

import org.scalatest.FunSpec
import org.apache.spark.sql.functions._
import com.github.mrpowers.spark.fast.tests.DataFrameComparer
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.Row
import ro.neghina.crime.transforms

class TransformsSpec
    extends FunSpec
      with DataFrameComparer
      with DataFrameSuiteBase {

  import spark.implicits._

  describe(".select") {
    it("select specific columns and gives an alias") {
      val input = Seq(
        ("Anita"),
        ("Carlos"),
        ("Luisa")
      ).toDF("NAME")
      val result = input.transform(transforms.select(Map("NAME" -> "name")))
      val expectedSchema = StructType(Seq(
        StructField("name", StringType, true)
      ))
      assert(result.schema === expectedSchema)
    }

    it("select only specific column and gives an alias") {
      val input = Seq(
        ("Anita", 30),
        ("Carlos", 35),
        ("Luisa", 41)
      ).toDF("NAME", "age")
      val result = input.transform(transforms.select(Map("NAME" -> "name")))
      val expectedSchema = StructType(Seq(
        StructField("name", StringType, true)
      ))
      val expectedData = List(
        Row("Anita"),
        Row("Carlos"),
        Row("Luisa")
      )
      val expectedDF = spark.createDataFrame(
        spark.sparkContext.parallelize(expectedData),
        expectedSchema
      )
      assertSmallDataFrameEquality(result, expectedDF)
    }
  }

  describe(".districtName") {
    it("extract district name from input file name") {
      val path = getClass.getResource("/data/2012-12/2012-12-new-york.csv").getPath
      val input = spark.read
        .option("header","true")
        .csv(path)
      val result = input.transform(transforms.districtName())

      val expectedSchema = StructType(Seq(
        StructField("name", StringType, true),
        StructField("age", StringType, true),
        StructField("districtName", StringType, true),
      ))
      val expectedData = List(
        Row("Anita", "30", "new york"),
        Row("Carlos", "35", "new york"),
        Row("Luisa", "41", "new york")
      )
      val expectedDF = spark.createDataFrame(
        spark.sparkContext.parallelize(expectedData),
        expectedSchema
      )

      result.show(false)

      assertSmallDataFrameEquality(result, expectedDF)
    }

    it("has value only if the dataframe is read from a file") {
      val input = Seq(
        ("Anita", 30),
        ("Carlos", 35),
        ("Luisa", 41)
      ).toDF("name", "age")
      val result = input.transform(transforms.districtName())

      val expectedSchema = StructType(Seq(
        StructField("name", StringType, true),
        StructField("age", IntegerType, false),
        StructField("districtName", StringType, true),
      ))
      val expectedData = List(
        Row("Anita", 30, ""),
        Row("Carlos", 35, ""),
        Row("Luisa", 41, "")
      )
      val expectedDF = spark.createDataFrame(
        spark.sparkContext.parallelize(expectedData),
        expectedSchema
      )

      assertSmallDataFrameEquality(result, expectedDF)
    }
  }

  describe(".lastOutcomeType") {
    it("select last outcome row") {
      val input = Seq(
        ("1", "2012-11", "Under investigation"),
        ("2", "2012-11", "Under investigation"),
        ("3", "2012-11", "Under investigation"),
        ("3", "2012-11", "Under investigation"),
        ("1", "2012-12", "Suspect charged"),
        ("2", "2012-12", "Under investigation")
      ).toDF("crimeId", "month", "outcome")

      val expected = Seq(
        ("3", "2012-11", "Under investigation"),
        ("1", "2012-12", "Suspect charged"),
        ("2", "2012-12", "Under investigation")
      ).toDF("crimeId", "month", "outcome")

      val result = input.transform(transforms.lastOutcomeType())

      result.show(false)
      expected.show(false)

      assertSmallDataFrameEquality(result, expected)
    }
  }

  describe(".lastOutcome") {
    it("should take not-null value") {
      val input = Seq(
        ("Under investigation", "Suspect charged"),
        ("Under investigation", null),
        (null, "Suspect charged"),
        (null, null),
      ).toDF("outcomeType", "lastOutcomeCategory")

      val expected = Seq(
        ("Under investigation", "Suspect charged", "Under investigation"),
        ("Under investigation", null, "Under investigation"),
        (null, "Suspect charged", "Suspect charged"),
        (null, null, null),
      ).toDF("outcomeType", "lastOutcomeCategory", "lastOutcome")

      val result = input.transform(transforms.lastOutcome())
      assertSmallDataFrameEquality(result, expected)
    }
  }
}
