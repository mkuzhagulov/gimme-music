package bot.utils

object Exceptions {
  case class IncorrectUrl(url: String) extends Exception
  case class FileTooLarge() extends Exception
  case class FfmpegError() extends Exception
  case class YoutubeDlError() extends Exception
}
