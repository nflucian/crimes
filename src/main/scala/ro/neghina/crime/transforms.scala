package ro.neghina.crime

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import functions._

object transforms {
  /**
   * Select specific columns and gives an alias
   *
   * @param fields key/value of selected column and his alias
   */
  def select(fields: Map[String, String])(df: DataFrame): DataFrame = {
    df.select(fields.map{ case (k,v) => col(k).as(v) }.toSeq : _*)
  }

  /**
   * Add districtName column extracted from input file name
   */
  def districtName()(df: DataFrame): DataFrame = {
    df.withColumn("path", split(input_file_name(),"/"))
      .withColumn("filename",
        col("path")(size(col("path")).minus(1))
      )
      .withColumn("districtName",
        remove("filename", Seq(
            "(\\d{4}\\-\\d{2}\\-)",
            ".csv",
            "street",
            "outcomes",
            "-"
          )
        )
      )
      .drop("path", "filename")
  }

  /**
   * Keep the last outcome of each crime
   */
  def lastOutcomeType()(df: DataFrame): DataFrame = {
    val windowSpec = Window.partitionBy("crimeId").orderBy(desc("month"))
    df.distinct()
      .withColumn("row_number", row_number().over(windowSpec))
      .where(col("row_number").eqNullSafe(lit("1")))
      .drop("row_number")
  }

  /**
   * The last outcome is taken from outcomes file `outcomeType` if exists,
   * otherwise is taken from street file `lastOutcomeCategory`
   */
  def lastOutcome()(df: DataFrame): DataFrame = {
    df.withColumn("lastOutcome", coalesce(col("outcomeType"), col("lastOutcomeCategory")))
  }
}
