package com.carher

import cats.effect.{Async, ContextShift}
import cats.implicits._
import com.carher.authentication.AuthenticationService
import com.carher.config.{JdbcConfig, JwtConfig}
import com.carher.error.{HttpErrorHandler, UserAccountHttpErrorHandler}
import com.carher.middleware.AuthMiddlewareJwt
import com.carher.repository.UserAccountDoobie
import com.carher.route.{UserAccountAuthedRoutes, UserAccountRoutes}
import com.carher.service.UserAccountServiceImpl
import com.softwaremill.macwire.wire
import com.typesafe.scalalogging.StrictLogging
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import org.http4s.HttpRoutes
import org.http4s.server.Router

class UserAccountHttp4sModule[F[_]: Async: ContextShift](cfg: JdbcConfig, apiPrefix: String, jwtConfig: JwtConfig) extends StrictLogging {
  val xa: Aux[F, Unit] = Transactor.fromDriverManager[F](cfg.driver, cfg.url, cfg.user, cfg.password)
  val userAccountRep : UserAccountDoobie[F] = wire[UserAccountDoobie[F]]
  val authService: AuthenticationService = new AuthenticationService(jwtConfig)
  val userAccountService: UserAccountServiceImpl[F] = wire[UserAccountServiceImpl[F]]
  val authMiddleware: AuthMiddlewareJwt[F] = wire[AuthMiddlewareJwt[F]]
  val errorHandler: HttpErrorHandler[F] = wire[UserAccountHttpErrorHandler[F]]

  val routes: HttpRoutes[F] = Router(apiPrefix -> errorHandler.handle(wire[UserAccountRoutes[F]].routes <+> wire[UserAccountAuthedRoutes[F]].authRoutes))
}
