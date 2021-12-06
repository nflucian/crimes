name := "crimes-proj"

version := "0.0.1"

scalaVersion := "2.12.12"

val sparkVersion = "3.1.1"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"

libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1"
libraryDependencies += "org.elasticsearch" %% "elasticsearch-spark-30" % "7.15.0"
libraryDependencies += "com.github.mrpowers" %% "spark-fast-tests" % "0.21.3" % "test"
libraryDependencies += "com.holdenkarau" %% "spark-testing-base" % "3.2.0_1.1.1" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

// test suite settings
fork in Test := true
javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
// Show runtime of tests
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")

// don't include Scala in the JAR file
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
