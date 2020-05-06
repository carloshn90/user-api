package middleware

import authentication.AuthenticationService
import cats.data.Kleisli
import cats.effect.Sync
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import pdi.jwt.JwtClaim

class AuthMiddleware[F[_]: Sync] {

  def to(service: HttpRoutes[F]): HttpRoutes[F] = Kleisli {
    req: Request[F] => {

      service(req).map { resp =>
        authenticateJwtToken(req) match {
          case Some(_)  => resp
          case None     => Response[F](Forbidden)
        }
      }

    }
  }

  private def authenticateJwtToken(req: Request[F]): Option[JwtClaim] = for {
      token <- getToken(req)
      auth  <- AuthenticationService.decodeToken(token)
    } yield auth

  private def getToken(req: Request[F]): Option[String] = for {
      header <- req.headers.get(Authorization)
    } yield header.value.replace("Bearer ", "")

}
