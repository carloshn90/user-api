package com.carher.route

import cats.effect.Sync
import cats.implicits._
import com.carher.json.CirceJsonCodecs
import com.carher.payload.LoginRequestPayload
import com.carher.service.UserAccountService
import org.http4s.{Challenge, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`WWW-Authenticate`

class UserAccountRoutes[F[_]: Sync](userAccountService: UserAccountService[F]) extends Http4sDsl[F] with CirceJsonCodecs {

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "login" =>
      for {
        loginReq  <- req.as[LoginRequestPayload]
        userInDb  <- userAccountService.login(loginReq)
        resp      <- userInDb.fold(
          Unauthorized(`WWW-Authenticate`(Challenge("Basic", "Access to the staging site", Map("charset" -> "UTF-8"))))
        )(Ok(_))
      } yield resp
  }
}
