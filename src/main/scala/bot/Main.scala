package bot

import AppGlobals._

import scala.util.{Failure, Success}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("main-actor")
  implicit val executionContext: concurrent.ExecutionContext = system.dispatcher

  val log = Logging(system.eventStream, "MusicBot")

  val token = config.getString("bot.token")
  val bot = new BotWithPolling(token)

  log.info("Bot starting...")
  bot.run() onComplete {
    case Success(_) => system.terminate()
    case Failure(ex) =>
      log.error("Bot was unexpected stopped")
      system.terminate()
  }
}
