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
  "com.softwaremill.sttp" %% "okhttp-backend" % "1.7.2"
)

mainClass in assembly := Some("bot.Main")

assemblyMergeStrategy in assembly := {
  case "META-INF/io.netty.versions.properties" => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
