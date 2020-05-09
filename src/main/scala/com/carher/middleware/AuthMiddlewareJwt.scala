package com.carher.middleware

import cats.data.{Kleisli, OptionT}
import cats.effect.Async
import cats.implicits._
import com.carher.authentication.AuthenticationService
import com.carher.payload.JwtUserPayload
import com.typesafe.scalalogging.StrictLogging
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import org.http4s.server._
import org.http4s.{AuthedRoutes, Request, Response}

class AuthMiddlewareJwt[F[_]: Async](authService: AuthenticationService) extends StrictLogging {

  def authMiddleware: AuthMiddleware[F, JwtUserPayload] = AuthMiddleware(auth, onFailure)

  private val auth: Kleisli[F, Request[F], Either[String, JwtUserPayload]] = Kleisli {
    req: Request[F] => {
      val authenticationJwt: Either[String, JwtUserPayload] = authenticateJwtToken(req)
      authenticationJwt.left.map(error => logger.info(s"$error in the route ${req.uri}"))
      authenticationJwt.pure[F]
    }
  }

  private val onFailure: AuthedRoutes[String, F] =
    Kleisli(_ => OptionT.some(Response(Forbidden)))

  private def authenticateJwtToken(req: Request[F]): Either[String, JwtUserPayload] = for {
      token <- getToken(req)
      auth <- authService.decodeToken(token)
    } yield auth


  private def getToken(req: Request[F]): Either[String, String] = for {
      header <- req.headers.get(Authorization).toRight("Authorization header not found")
    } yield authService.getTokenFromHeader(header.value)

}
