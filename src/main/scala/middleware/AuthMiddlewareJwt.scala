package middleware

import cats.data.{Kleisli, OptionT}
import cats.implicits._
import org.http4s.{AuthedRoutes, Request, Response}
import org.http4s.server._
import org.http4s.headers.Authorization
import authentication.AuthenticationService
import cats.effect.Async
import config.JwtConfig
import pdi.jwt.JwtClaim

import org.http4s.dsl.io._

class AuthMiddlewareJwt[F[_]: Async](authService: AuthenticationService, jwtConfig: JwtConfig) {

  def authMiddleware: AuthMiddleware[F, JwtClaim] = AuthMiddleware(auth, onFailure)

  private val auth: Kleisli[F, Request[F], Either[String, JwtClaim]] = Kleisli {
    req: Request[F] => {
      authenticateJwtToken(req).toRight("Forbidden").pure[F]
    }
  }

  private val onFailure: AuthedRoutes[String, F] =
    Kleisli(_ => OptionT.some(Response(Forbidden)))

  private def authenticateJwtToken(req: Request[F]): Option[JwtClaim] = for {
      token <- getToken(req)
      auth  <- authService.decodeToken(token, jwtConfig.password)
    } yield auth

  private def getToken(req: Request[F]): Option[String] = for {
      header <- req.headers.get(Authorization)
    } yield header.value.replace(jwtConfig.prefix, "")

}
