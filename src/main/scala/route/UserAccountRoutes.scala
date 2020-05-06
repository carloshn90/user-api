package route

import cats.effect.Sync
import cats.implicits._
import json.CirceJsonCodecs
import model.{UserAccount, UserAccountResult}
import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl
import payload.LoginRequestPayload
import pdi.jwt.JwtClaim
import service.UserAccountServiceImpl

class UserAccountRoutes[F[_]: Sync](userAccountService: UserAccountServiceImpl[F]) extends Http4sDsl[F] with CirceJsonCodecs {

  val routes: AuthedRoutes[JwtClaim, F] = AuthedRoutes.of[JwtClaim, F] {

    case req @ POST -> Root as _ =>
      for {
        userAccount <- req.req.as[UserAccount]
        userInDb    <- userAccountService.insert(userAccount)
        resp        <- Ok(UserAccountResult(userInDb))
      } yield resp

    case req @ PUT -> Root / IntVar(id) as _ =>
      for {
        userAccount <- req.req.as[UserAccount]
        userInDb    <- userAccountService.update(id, userAccount)
        resp        <- Ok(UserAccountResult(userInDb))
      } yield resp

    case DELETE -> Root / IntVar(id) as _ =>
      for {
        userDelete  <- userAccountService.delete(id)
        resp        <- Ok(UserAccountResult(userDelete))
      } yield resp

    case req @ POST -> Root / "login" as _ =>
      for {
        loginReq  <- req.req.as[LoginRequestPayload]
        userInDb  <- userAccountService.login(loginReq)
        resp      <- userInDb.fold(NotFound())(Ok(_))
      } yield resp
  }

}
