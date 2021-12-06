package ro.neghina.crime

import scopt.{DefaultOParserSetup, OParser}

/**
 * Encapsulate parsed commandline arguments.
 */
case class Args(input: String = "", output: String = "", es: Map[String, String] = Map())

/**
 * A simple command line argument parser.
 * Arguments should be key value properties (`--key value`).
 */
object Args {
  def apply(args: Array[String]): Option[Args] = {
    val builder = OParser.builder[Args]
    val parser = {
      import builder._
      OParser.sequence(
        programName("spark-submit ... <application-jar> "),
        note("Available options:"),
        opt[String]('i',"input")
          .required()
          .action((v, cfg) => cfg.copy(input = v))
          .text("directory path"),
        opt[String]('o',"output")
          .required()
          .action((v, cfg) => cfg.copy(output = v))
          .text("elasticsearch index"),
        opt[Map[String, String]]("es")
          .valueName("k1=v1,k2=v2,...")
          .action((v,cfg) => cfg.copy(es = v))
          .text("elasticsearch arguments"),
        help("help").text("prints this usage text")
      )
    }

    val setup = new DefaultOParserSetup() {
      override def showUsageOnError: Option[Boolean] = Some(true)
      override def errorOnUnknownArgument: Boolean = false
    }

    OParser.parse(parser, args, Args(), setup)
  }
}