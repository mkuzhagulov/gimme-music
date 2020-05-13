package bot

import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.clients.FutureSttpClient
import com.bot4s.telegram.future.TelegramBot
import com.softwaremill.sttp.okhttp.OkHttpFutureBackend

import scala.concurrent.Future

class AbstractBot(token: String) extends TelegramBot {
  implicit val backend = OkHttpFutureBackend()
  override val client: RequestHandler[Future] = new FutureSttpClient(token)
}
