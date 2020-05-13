package bot

import bot.utils.RoflStuff
import com.bot4s.telegram.api.declarative.{Commands, Messages}
import com.bot4s.telegram.future.Polling
import com.bot4s.telegram.methods.SendMessage
import com.bot4s.telegram.models.Message

import scala.concurrent.Future

class BotWithPolling(token: String)
  extends AbstractBot(token)
    with Polling
    with Commands[Future]
    with Messages[Future]
    with RoflStuff {

  def matchMessage(msg: Message): Future[Message] = {
    if (msg.sticker.isDefined) sendRandomSticker(msg.source)
    else if (msg.text.isDefined) {
      msg.text.get.toLowerCase() match {
        case "да" | "нет" => yesOrNo(msg)
        case "/help" | "/start" => start(msg)
        case _ => request(SendMessage(msg.source, "В разработке ..."))
      }
    }
    else request(SendMessage(msg.source, "Я не понимаю таких команд =("))
  }

  override def receiveMessage(msg: Message): Future[Unit] = {
    matchMessage(msg)
    Future.successful()
  }
}
