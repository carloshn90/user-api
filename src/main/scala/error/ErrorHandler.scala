package error

import cats.ApplicativeError
import cats.implicits._
import cats.data.{Kleisli, OptionT}
import org.http4s.{HttpRoutes, Request, Response}

object ErrorHandler {

  def apply[F[_], E <: Throwable](routes: HttpRoutes[F])(handler: E => F[Response[F]])(implicit ae: ApplicativeError[F, E]): HttpRoutes[F] =
    Kleisli { req: Request[F] =>
      OptionT {
        routes.run(req).value.handleErrorWith { e =>
          handler(e).map(Option(_))
        }
      }
    }

}
