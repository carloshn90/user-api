import config.AppConfig
import cats.Applicative
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, SyncIO, Timer}
import cats.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import fs2.Stream

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object UserAccountServer extends IOApp.WithContext {

  val ec: ExecutionContextExecutor = ExecutionContext.global

  val (server, jdbc, _) =
    AppConfig.load.fold(e => sys.error(s"Failed to load configuration:\n${e.toList.mkString("\n")}"), identity)

  override def run(args: List[String]): IO[ExitCode] = stream[IO].compile.drain.as(ExitCode.Success)

  def stream[F[_]: ConcurrentEffect: Applicative: ContextShift: Timer]: Stream[F, ExitCode] =
    for {
      mod <- Stream.eval(new UserAccountHttp4sModule(jdbc, server.apiUrl).pure[F])

      apiV1App = mod.routes.orNotFound

      exitCode <- BlazeServerBuilder[F](ec)
        .bindHttp(server.port, server.host)
        .withHttpApp(apiV1App)
        .serve
    } yield exitCode

  override protected def executionContextResource: Resource[SyncIO, ExecutionContext] = Resource.liftF(SyncIO(ec))
}
