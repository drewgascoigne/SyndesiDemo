name := "Project"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  "mysql" % "mysql-connector-java" % "5.1.18",
   "com.typesafe" %% "play-plugins-mailer" % "2.2.0",
   "com.typesafe.akka" %% "akka-actor" % "2.3.2",
  anorm,
  cache
)     

play.Project.playScalaSettings
