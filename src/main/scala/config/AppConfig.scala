package config

import java.io.File

import pureconfig.generic.auto._
import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions, ConfigRenderOptions}
import com.typesafe.scalalogging.StrictLogging
import pureconfig.error.ConfigReaderFailures
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigSource}
import pureconfig.generic.ProductHint

final case class Server(apiUrl: String = "/", host: String = "localhost", port: Int = 8099)

object AppConfig extends StrictLogging {

  private val parseOptions = ConfigParseOptions.defaults().setAllowMissing(false)
  private val renderOptions = ConfigRenderOptions.defaults().setOriginComments(false)

  private val path = sys.env.getOrElse("APP_CONFIG_PATH", "src/main/resources/application.conf")

  implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  def load: Either[ConfigReaderFailures, (Server, JdbcConfig, Config)] = {
    val config = ConfigFactory.parseFile(new File(path), parseOptions).resolve()
    logger.debug("config content:\n {}", config.root().render(renderOptions))

    for {
      // validate storage config also
      j <- ConfigSource.fromConfig(config).at("storage").load[JdbcConfig]
      c <- ConfigSource.fromConfig(config).at("server").load[Server]
    } yield (c, j, config)
  }

}
