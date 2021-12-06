package ro.neghina.crime

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait SparkSessionWrapper extends Serializable {

  lazy val spark: SparkSession = {
    val sparkConfig = new SparkConf()
      .set("es.index.auto.create", "true")
    val confSpark = sparkConfig.setAppName("CrimeApp")
      .setMaster(sparkConfig.get("spark.master", "local[*]"))

    SparkSession.builder().config(confSpark).getOrCreate()
  }

}