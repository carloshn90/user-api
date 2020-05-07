package route

import cats.effect.Sync
import cats.implicits._
import json.CirceJsonCodecs
import middleware.AuthMiddlewareJwt
import model.{UserAccount, UserAccountResult}
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.AuthMiddleware
import payload.JwtUserPayload
import service.UserAccountServiceImpl

class UserAccountAuthedRoutes[F[_]: Sync](userAccountService: UserAccountServiceImpl[F],
                                          authMiddleware: AuthMiddlewareJwt[F]) extends Http4sDsl[F] with CirceJsonCodecs {

  val middleware: AuthMiddleware[F, JwtUserPayload] = authMiddleware.authMiddleware

  val authRoutes: HttpRoutes[F] = middleware(AuthedRoutes.of[JwtUserPayload, F] {

    case GET -> Root as jwtUser =>
      for {
        userAccount  <- userAccountService.findById(jwtUser.userId)
        resp        <- userAccount.fold(NotFound())(Ok(_))
      } yield resp

    case req @ POST -> Root as _ =>
      for {
        userAccount <- req.req.as[UserAccount]
        userInDb    <- userAccountService.insert(userAccount)
        resp        <- Ok(UserAccountResult(userInDb))
      } yield resp

    case req @ PUT -> Root as jwtUser =>
      for {
        userAccount <- req.req.as[UserAccount]
        userInDb    <- userAccountService.update(jwtUser.userId, userAccount)
        resp        <- Ok(UserAccountResult(userInDb))
      } yield resp

    case DELETE -> Root as jwtUser =>
      for {
        userDelete  <- userAccountService.delete(jwtUser.userId)
        resp        <- Ok(UserAccountResult(userDelete))
      } yield resp
  })
}
