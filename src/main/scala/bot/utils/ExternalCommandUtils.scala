package bot.utils

import bot.AppGlobals._

import scala.sys.process._
import java.io.File
import java.nio.file.{Files, Paths}

import Exceptions._
import org.slf4j.{Logger, LoggerFactory}

object ExternalCommandUtils {
  private val log: Logger = LoggerFactory.getLogger(getClass)

  val downloadPath = config.getString("bot.download-dir")
  val FileSizeLimit = 50 * 1024 * 1024

  private def getListOfFiles(dir: String): List[String] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList.map(x => x.getName)
    } else {
      List[String]()
    }
  }

  private def commandFunction(commands: Seq[String]): Either[Throwable, String] = {
    try {
      Right(commands.!!)
    } catch {
      case ex: Throwable => Left(ex)
    }
  }

  private def download(url: String): Either[Throwable, String] = commandFunction(Seq("youtube-dl", "-o", s"$downloadPath/%(title)s.%(ext)s", url))

  private def getTitle(url: String): Either[Throwable, String] = commandFunction(Seq("youtube-dl", "--get-file", "-o", s"%(title)s.%(ext)s", url))

  private def convertToMp3(filename: String, title: String): Either[Throwable, String] = {
    // cut file extension
    commandFunction(Seq("ffmpeg", "-i", s"$downloadPath/$filename", "-ac", "2", s"$downloadPath/$title.mp3"))
    removeFile(filename)
  }

  private def removeFile(filename: String): Either[Throwable, String] = {
    commandFunction(Seq("rm", s"$downloadPath/$filename"))
  }

  def obtainAudioProcess(url: String): (String, Array[Byte]) = {
    val title = getTitle(url) match {
      case Left(_) => throw IncorrectUrl(url)
      case Right(value) => value.split('.').dropRight(1).mkString(".")
    }

    download(url) match {
      case Left(ex) => log.error(ex.getMessage); throw YoutubeDlError()
      case Right(value) => log.debug(value)
    }
    log.info(s"Файл $title успешно скачан чере youtoube-dl")

    val allFiles = getListOfFiles(downloadPath)

    val fileName = allFiles.filter(_.startsWith(title)) match {
      case Nil => log.error(s"Файла $title не существует"); throw YoutubeDlError()
      case xs :: _ => xs
    }

    log.info(fileName)

    val fileSize = Files.readAllBytes(Paths.get(s"$downloadPath/$fileName")).length

    if (fileSize > FileSizeLimit) {
      log.info(s"File '$fileName' too large. $fileSize bytes")
      removeFile(s"$fileName")
      throw FileTooLarge()
    }

    val byteArray = if (fileName.endsWith("mp3")) Files.readAllBytes(Paths.get(s"$downloadPath/$fileName"))

    else {
      convertToMp3(fileName, title) match {
        case Left(_) => log.error("Ошибка конвертации в mp3"); throw YoutubeDlError()
        case Right(value) => log.debug(value)
      }
      Files.readAllBytes(Paths.get(s"$downloadPath/$title.mp3"))
    }

    removeFile(s"$title.mp3")

    (title, byteArray)

  }
}
