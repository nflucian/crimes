package ro.neghina.crime

import org.apache.spark.sql.Column
import org.apache.spark.sql.functions._

object functions {

  /**
   * Remove substring from a column based on multiple RegExp
   *
   * @param colName string the column name
   * @param regexp list of regexp
   */
  def remove(colName: String, regexp: Seq[String]): Column = {
    regexp.foldLeft(col(colName))( (acc, exp) => trim(regexp_replace(acc, exp, " ")) )
  }

}
