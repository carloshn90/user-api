package com.carher.config

import java.io.File

import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions, ConfigRenderOptions}
import com.typesafe.scalalogging.StrictLogging
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._
import pureconfig.generic.ProductHint
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigSource}

final case class Server(apiUrl: String = "/", host: String = "localhost", port: Int = 8099)

object AppConfig extends StrictLogging {

  private val parseOptions = ConfigParseOptions.defaults().setAllowMissing(false)
  private val renderOptions = ConfigRenderOptions.defaults().setOriginComments(false)

  private val path: String = sys.env.getOrElse("APP_CONFIG_PATH", "src/main/resources/application.conf")

  implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  def load: Either[ConfigReaderFailures, (Server, JdbcConfig, JwtConfig, Config)] = {
    val config = ConfigFactory.parseFile(new File(path), parseOptions).resolve()
    logger.debug("config content:\n {}", config.root().render(renderOptions))

    for {
      // validate storage config also
      db      <- ConfigSource.fromConfig(config).at("storage").load[JdbcConfig]
      server  <- ConfigSource.fromConfig(config).at("server").load[Server]
      jwt     <- ConfigSource.fromConfig(config).at("jwt").load[JwtConfig]

    } yield (server, db, jwt, config)
  }

}
