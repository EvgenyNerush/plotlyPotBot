name := "plotlyPotBot"
version := "0.0.0"
scalaVersion := "2.13.8"

val akkaVersion = "2.6.19"
val akkaHttpVersion = "10.2.9"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "org.scalacheck" %% "scalacheck" % "1.16.0" % "test",
)
