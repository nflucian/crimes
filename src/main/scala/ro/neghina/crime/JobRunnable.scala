package ro.neghina.crime

/** Runner specific job. */
trait JobRunnable extends SparkSessionWrapper {
  def run(args: Args): Unit
}
