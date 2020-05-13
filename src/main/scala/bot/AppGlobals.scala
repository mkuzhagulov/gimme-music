package bot

import com.typesafe.config.{Config, ConfigFactory}

object AppGlobals {
  val config: Config = ConfigFactory.load()

}
