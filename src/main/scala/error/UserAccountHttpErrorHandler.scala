package error

import cats.MonadError
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl

class UserAccountHttpErrorHandler[F[_]](implicit M: MonadError[F, Throwable]) extends HttpErrorHandler[F, UserAccountError] with Http4sDsl[F] {

  private val handler: Throwable => F[Response[F]] = {
    case InvalidUserAccount(t, m) => BadRequest(s"Invalid user account: $t. Cause: $m")
    case t => InternalServerError(s"Internal server error: $t")
  }

  override def handle(routes: HttpRoutes[F]): HttpRoutes[F] = ErrorHandler(routes)(handler)
}
