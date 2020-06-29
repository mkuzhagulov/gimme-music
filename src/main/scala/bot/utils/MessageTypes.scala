package bot.utils

import bot.AppGlobals._
import bot.utils.Exceptions._
import com.bot4s.telegram.api.BotBase
import com.bot4s.telegram.methods.{SendAnimation, SendAudio, SendMessage, SendSticker}
import com.bot4s.telegram.models.{InputFile, Message}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.util.Random

trait MessageTypes { this: BotBase[Future] =>
  case class AudioInfo(title: String, file: Array[Byte])

  private val log: Logger = LoggerFactory.getLogger(getClass)

  val randomPhrases =  Vector(
    "Тэкс, что тут у нас...",
    "Абракадабра...",
    "Please stand by...",
    "А ты что думал, все так быстро?",
    "Сходи пока завари кофеек",
    "Можешь сходить пока покурить",
    "Три икс в кубе плюс константа... Ну что там?",
    "Пока ты ждешь трек, я пашу как лошадь",
    "Мда, ну и вкусы у тебя"
  )

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
    val helpText = "Для получения файла отправь ссылку на youtube, soundcloud или mixcloud. Учти, что телеграм имеет " +
      "ограничения на размер пересылаемых через бот файлов больше 50 Мб =("
    msg.text.get match {
      case "/start" =>
        request(SendMessage(msg.source, helpText))
        request(SendAnimation(msg.source, InputFile(gif)))
      case "/help" => request(SendMessage(msg.source, helpText))
    }
  }

  /**
   * Obtain audio file and send it to user
   *
   * @param msg Telegram message
   * @param fileInfo Audio file info
   * @return Message packed in Future
   */
  def sendMusic(msg: Message, fileInfo: AudioInfo): Future[Message] = {
    val fixedTitle = fileInfo.title.replaceAll("[^\\w\\d\\t\\n\\v\\f\\rа-яА-Я ()-_]", "") match {
      case "" => s"Performing Artist #${Random.nextInt(100)} - ID"
      case other => other
    }
    val audio = SendAudio(
      msg.source,
      InputFile(fixedTitle, fileInfo.file)
    )

    log.info(s"Audio File formed $audio")
    request(audio)
  }

  def exceptionHandler(msg: Message, ex: Throwable): Future[Message] = ex match {
    case IncorrectUrl(_) => request(SendMessage(msg.source, "Бот не смог обработать данный URL, попробуйте другой"))
    case FileTooLarge() => request(SendMessage(msg.source, "Получившийся файл слишком большой. Он должен быть меньше 50 Мб."))
    case _ => request(SendMessage(msg.source, "Внутрення ошибка бота =( \nНапиши автору, если получил(а) данное сообщение"))
  }
}
