package bot

import AppGlobals._

import scala.util.{Failure, Success}
import akka.actor.{ActorRef, ActorSystem, Props}

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("main-actor")
  implicit val executionContext: concurrent.ExecutionContext = system.dispatcher

  val token = config.getString("bot.token")
  val bot = new BotWithPolling(token)

  bot.run() onComplete {
    case Success(_) => println("Success"); system.terminate()
    case Failure(ex) => println(s"Error: ${ex.getMessage}"); system.terminate()
  }
}
