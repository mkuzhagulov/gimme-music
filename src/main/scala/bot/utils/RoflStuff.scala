package bot.utils

import bot.AppGlobals._
import com.bot4s.telegram.api.BotBase
import com.bot4s.telegram.methods.{SendAnimation, SendMessage, SendSticker}
import com.bot4s.telegram.models.{InputFile, Message}

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

trait RoflStuff { this: BotBase[Future] =>
  def sendRandomSticker(chatId: Long): Future[Message] = {
    val setOfStickers = config.getStringList("bot.stickers").asScala

    request(SendSticker(chatId, InputFile(setOfStickers(Random.nextInt(setOfStickers.length)))))
  }

  def yesOrNo(msg: Message): Future[Message] = {
    val yes = config.getString("bot.da")
    val no = config.getString("bot.net")

    msg.text.get.toLowerCase() match {
      case "да" => request(SendSticker(msg.source, InputFile(yes)))
      case "нет" => request(SendSticker(msg.source, InputFile(no)))
    }
  }

  def start(msg: Message): Future[Message] = {
    val gif = config.getString("bot.welcome-gif")
    val helpText = "Для получения файла отправь ссылку на youtube, soundcloud или mixcloud трека"
    msg.text.get match {
      case "/start" =>
        request(SendMessage(msg.source, helpText + "\n" + "Да, и welcome to the club buddy..."))
        request(SendAnimation(msg.source, InputFile(gif)))
      case "/help" => request(SendMessage(msg.source, helpText))
    }
  }
}
