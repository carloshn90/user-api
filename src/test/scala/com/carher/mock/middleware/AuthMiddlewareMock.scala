package com.carher.mock.middleware

import cats.data.{Kleisli, OptionT}
import cats.effect.Async
import cats.implicits._
import com.carher.authentication.AuthenticationService
import com.carher.config.JwtConfig
import com.carher.middleware.AuthMiddlewareJwt
import com.carher.payload.JwtUserPayload
import org.http4s.dsl.io.Forbidden
import org.http4s.server.AuthMiddleware
import org.http4s.{AuthScheme, AuthedRoutes, Request, Response}

class AuthMiddlewareMock[F[_]: Async] {

  private val jwtConfig: JwtConfig = JwtConfig(password = "test", prefix = AuthScheme.Bearer.toString(), expirationSeconds = 60)
  private val authenticationService: AuthenticationService = new AuthenticationService(jwtConfig)
  private val onFailure: AuthedRoutes[String, F] = Kleisli(_ => OptionT.some(Response(Forbidden)))

  val success: AuthMiddlewareJwt[F] = new AuthMiddlewareJwt[F](authenticationService) {

    override def authMiddleware: AuthMiddleware[F, JwtUserPayload] = AuthMiddleware(auth, onFailure)

    val auth: Kleisli[F, Request[F], Either[String, JwtUserPayload]] = Kleisli(_ =>
      Either.right[String, JwtUserPayload](JwtUserPayload(1)).pure[F]
    )
  }

  val forbidden: AuthMiddlewareJwt[F] = new AuthMiddlewareJwt[F](authenticationService) {

    override def authMiddleware: AuthMiddleware[F, JwtUserPayload] = AuthMiddleware(auth, onFailure)

    val auth: Kleisli[F, Request[F], Either[String, JwtUserPayload]] = Kleisli(_ =>
      Either.left[String, JwtUserPayload]("Error!").pure[F]
    )
  }

}
