name := """pimly-gmail"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.1",
  "com.typesafe.akka" %% "akka-slf4j" % "2.2.1",
  "com.typesafe" %% "play-plugins-mailer" % "2.2.0",
  "com.typesafe.play" %% "play-cache" % "2.2.0",
  "org.webjars" %% "webjars-play" % "2.2.0",
  "org.webjars" % "bootstrap" % "2.3.1",
  "org.webjars" % "flot" % "0.8.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.1" % "test",
  "com.sun.mail" % "javax.mail" % "1.4.7",
  "javax.mail" % "mail" % "1.4.7",
    "com.sun.mail" % "gimap" % "1.4.7",
    "com.netflix.astyanax" % "astyanax-cassandra" % "1.56.43",
    "com.netflix.astyanax" % "astyanax-thrift" % "1.56.43",
    "com.netflix.astyanax" % "astyanax-entity-mapper" % "1.56.43"
)

play.Project.playScalaSettings
