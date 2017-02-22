name := "SparkParser"

scalaVersion := "2.11.8"
val kafkaVersion = "0.10.0.1"
val sparkVersion = "2.0.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",  
  "org.apache.kafka" %% "kafka" % kafkaVersion % "provided",
  "org.apache.kafka" % "kafka-clients" % kafkaVersion % "provided",
  "com.typesafe" % "config" % "1.3.1",
  "org.apache.spark" % "spark-sql_2.11" % sparkVersion % "provided",
  "com.typesafe.play" %% "play-json" % "2.3.4",
  ("org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion) exclude ("org.spark-project.spark", "unused")
  
)


resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray repository"    at "http://repo.spray.io/",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
)

resolvers += "Typesafe Simple Repository" at "http://repo.typesafe.com/typesafe/simple/maven-releases/"

resolvers += Resolver.sonatypeRepo("releases")

resolvers += Resolver.sonatypeRepo("snapshots")


assemblyJarName in assembly := name.value + ".jar"
