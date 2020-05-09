package com.carher.error

import cats.MonadError
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response}

class UserAccountHttpErrorHandler[F[_]](implicit M: MonadError[F, Throwable]) extends HttpErrorHandler[F] with Http4sDsl[F] {

  private val handler: Throwable => F[Response[F]] = {
    t => InternalServerError(s"Internal server error: $t")
  }

  override def handle(routes: HttpRoutes[F]): HttpRoutes[F] = ErrorHandler(routes)(handler)
}
