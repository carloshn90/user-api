package route

import cats.effect.Sync
import cats.implicits._
import json.CirceJsonCodecs
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import payload.LoginRequestPayload
import service.UserAccountServiceImpl

class UserAccountRoutes[F[_]: Sync](userAccountService: UserAccountServiceImpl[F]) extends Http4sDsl[F] with CirceJsonCodecs {

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "login" =>
      for {
        loginReq  <- req.as[LoginRequestPayload]
        userInDb  <- userAccountService.login(loginReq)
        resp      <- userInDb.fold(NotFound())(Ok(_))
      } yield resp
  }
}
