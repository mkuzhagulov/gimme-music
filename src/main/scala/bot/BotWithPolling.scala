package bot

import bot.utils.{ExternalCommandUtils, MessageTypes}
import com.bot4s.telegram.api.declarative.{Commands, Messages}
import com.bot4s.telegram.future.Polling
import com.bot4s.telegram.methods.SendMessage
import com.bot4s.telegram.models.Message
import scala.util.{Try,Success,Failure}

import scala.concurrent.Future
import scala.util.Random

class BotWithPolling(token: String)
  extends AbstractBot(token)
    with Polling
    with Commands[Future]
    with Messages[Future]
    with MessageTypes {

  private val URIPattern = "^(?i)(?:https?://)(?:www\\.)?(?:[A-Za-z0-9._%+-]+)/?.*$".r
  private val YesOrNoPattern = "^(?:[дД]а|[Нн]ет)$".r

  def matchMessage(msg: Message): Future[Message] = {
    if (msg.sticker.isDefined) sendRandomSticker(msg.source)
    else if (msg.text.isDefined) {
      msg.text.get match {
        case YesOrNoPattern() => yesOrNo(msg)
        case "/help" | "/start" => start(msg)
        case url@URIPattern() =>
          request(SendMessage(msg.source, randomPhrases(Random.nextInt(randomPhrases.length))))

          Try(ExternalCommandUtils.obtainAudioProcess(url)) match {
            case Success(res) => sendMusic(msg, AudioInfo(res._1, res._2))
            case Failure(ex) => exceptionHandler(msg, ex)
          }

        case _ => request(SendMessage(msg.source, "Некорректный URL"))
      }
    }
    else request(SendMessage(msg.source, "Я не понимаю таких команд =("))
  }

  override def receiveMessage(msg: Message): Future[Unit] = {
    matchMessage(msg)
    Future.successful()
  }
}
