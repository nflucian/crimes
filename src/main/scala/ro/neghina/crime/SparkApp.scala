package ro.neghina.crime

/** Main class for programmatically interacting */
trait SparkApp extends JobRunnable {
  def main(cmdlineArgs: Array[String]): Unit = {
    Args(cmdlineArgs) match {
      case Some(args) => run(args)
      case None => throw new IllegalArgumentException("Failed to load specific args")
    }
  }
}
