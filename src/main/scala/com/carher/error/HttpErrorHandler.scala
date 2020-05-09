package com.carher.error

import org.http4s.HttpRoutes

trait HttpErrorHandler[F[_]] {
  def handle(routes: HttpRoutes[F]): HttpRoutes[F]
}
