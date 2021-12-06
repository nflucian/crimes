package ro.neghina.crimes

import org.scalatest.FunSpec
import org.apache.spark.sql.functions._
import com.github.mrpowers.spark.fast.tests.ColumnComparer
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import ro.neghina.crime.functions

class FunctionsSpec
    extends FunSpec
    with DataFrameSuiteBase
    with ColumnComparer {

  import spark.implicits._

  describe("remove") {

    it("should delete substring from a column") {
      val regexps = Seq(
        "(\\d{4}\\-\\d{2}\\-)",
        ".csv",
        "street",
        "outcomes",
        "-"
      )
      val data = Seq(
        ("2012-12-new-york-street.csv", "new york"),
        ("bucharest-outcomes.csv", "bucharest"),
        ("2012-12-new-york.csv", "new york"),
        (null, null),
        ("", ""),
        ("new york", "new york")
      )
      val df = data
        .toDF("filename", "expected")
        .withColumn("actual", functions.remove("filename", regexps))

      assertColumnEquality(df, "actual", "expected")
    }
  }

}

