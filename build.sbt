enablePlugins(JavaServerAppPackaging)

val telegramBotVersion = "4.4.0-RC2"
val akkaVersion = "2.6.1"
val akkaHttpVersion = "10.1.11"

name := "GimmeMusicBot"

version in ThisBuild := "0.1"

scalaVersion := "2.12.11"

libraryDependencies ++= Seq(
  "com.bot4s" %% "telegram-core" % telegramBotVersion,
  "com.bot4s" %% "telegram-akka" % telegramBotVersion,
  "com.softwaremill.sttp.client" %% "core" % "2.1.1",
  "com.softwaremill.sttp" %% "okhttp-backend" % "1.7.2",
  "commons-io" % "commons-io" % "2.6",
  "io.scalaland" %% "chimney" % "0.5.2",
  "org.slf4j" % "slf4j-api" % "1.7.30",
  "ch.qos.logback"    %  "logback-classic" % "1.2.3",
  "tech.sparse" %%  "translit-scala" % "0.1.1"
)

mainClass in assembly := Some("bot.Main")

assemblyMergeStrategy in assembly := {
  case "META-INF/io.netty.versions.properties" => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
