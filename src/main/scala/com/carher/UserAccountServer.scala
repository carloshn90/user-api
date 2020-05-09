package com.carher

import cats.Applicative
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, SyncIO, Timer}
import cats.implicits._
import com.carher.config.AppConfig
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object UserAccountServer extends IOApp.WithContext {

  val ec: ExecutionContextExecutor = ExecutionContext.global

  val (server, jdbc, jwtConf, _) =
    AppConfig.load.fold(e => sys.error(s"Failed to load configuration:\n${e.toList.mkString("\n")}"), identity)

  override def run(args: List[String]): IO[ExitCode] = stream[IO].compile.drain.as(ExitCode.Success)

  def stream[F[_]: ConcurrentEffect: Applicative: ContextShift: Timer]: Stream[F, ExitCode] =
    for {
      mod <- Stream.eval(new UserAccountHttp4sModule(jdbc, server.apiUrl, jwtConf).pure[F])

      apiV1App = mod.routes.orNotFound

      exitCode <- BlazeServerBuilder[F](ec)
        .bindHttp(server.port, server.host)
        .withHttpApp(apiV1App)
        .serve
    } yield exitCode

  override protected def executionContextResource: Resource[SyncIO, ExecutionContext] = Resource.liftF(SyncIO(ec))
}
