package route

import cats.effect.Sync
import cats.implicits._
import json.CirceJsonCodecs
import middleware.AuthMiddlewareJwt
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.AuthMiddleware
import payload.{JwtUserPayload, UserAccountPayload, UserAccountResultPayload}
import service.UserAccountServiceImpl
import validation.validationUtil

class UserAccountAuthedRoutes[F[_]: Sync](userAccountService: UserAccountServiceImpl[F],
                                          authMiddleware: AuthMiddlewareJwt[F]) extends Http4sDsl[F] with CirceJsonCodecs {

  val middleware: AuthMiddleware[F, JwtUserPayload] = authMiddleware.authMiddleware

  val authRoutes: HttpRoutes[F] = middleware(AuthedRoutes.of[JwtUserPayload, F] {

    case GET -> Root as jwtUser =>
      for {
        userAccount   <- userAccountService.findById(jwtUser.userId)
        resp          <- userAccount.fold(NotFound())(Ok(_))
      } yield resp

    case req @ POST -> Root as _ =>
      for {
        userPayload <- req.req.as[UserAccountPayload]
        userInDb    <- userAccountService.insert(userPayload)
        resp        <- userInDb.fold(
          err => BadRequest(validationUtil.getValidationErrors(err)),
          ok => Ok(UserAccountResultPayload(ok))
        )
      } yield resp

    case req @ PUT -> Root as jwtUser =>
      for {
        userPayload <- req.req.as[UserAccountPayload]
        userInDb    <- userAccountService.update(jwtUser.userId, userPayload)
        resp        <- userInDb.fold(
          err => BadRequest(validationUtil.getValidationErrors(err)),
          ok => Ok(UserAccountResultPayload(ok))
        )
      } yield resp

    case DELETE -> Root as jwtUser =>
      for {
        userDelete  <- userAccountService.delete(jwtUser.userId)
        resp        <- Ok(UserAccountResultPayload(userDelete))
      } yield resp
  })
}
