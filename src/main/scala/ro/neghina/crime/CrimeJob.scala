package ro.neghina.crime

import org.apache.spark.sql.functions.col
import org.elasticsearch.spark.sql._
import transforms._
import org.locationtech.geomesa.spark.jts._

/**
 * Crime Job what processes UK street crimes
 */
object CrimeJob extends SparkApp {
  val csvOptions = Map(
    "header" -> "true",
    "inferSchema" -> "true"
  )

  val myQuery ="""{
            |"aggs" : {
            |    "crimeTypes" : {
            |        "terms" : { "field" : "crimeType" }
            |    }
            |}
            |}""".stripMargin

  override def run(args: Args): Unit = {
    val dfStreet = spark.read
      .options(csvOptions)
      .csv(s"${args.input}*/*-street.csv")
      .transform(select(Map(
        "Crime ID" -> "crimeId",
        "Latitude" -> "latitude",
        "Longitude" -> "longitude",
        "Crime type" -> "crimeType",
        "Last outcome category" -> "lastOutcomeCategory")))
      .transform(districtName())

    val dfOutcome = spark.read
      .options(csvOptions)
      .csv(s"${args.input}*/*-outcomes.csv")
      .transform(select(Map(
        "Crime ID" -> "crimeId",
        "Month" -> "month",
        "Outcome type" -> "outcomeType")))
      .transform(lastOutcomeType())

    val df = dfStreet.join(dfOutcome, Seq("crimeId"), "left")
      .transform(lastOutcome())
      .select("crimeId", "districtName", "latitude", "longitude", "crimeType", "lastOutcome")
      .withColumn("crimePoint", st_makePoint(col("longitude"), col("latitude")))

    df.saveToEs(args.output, args.es)
  }
}
