package route

import cats.effect.{Sync}
import cats.implicits._
import error.{HttpErrorHandler, UserAccountError}
import json.CirceJsonCodecs
import model.{UserAccount, UserAccountResult}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import payload.LoginRequestPayload
import service.UserAccountServiceImpl

class UserAccountRoutes[F[_]: Sync](userAccountService: UserAccountServiceImpl[F])
                                   (implicit httpErr: HttpErrorHandler[F, UserAccountError]) extends Http4sDsl[F] with CirceJsonCodecs {

  val routes: HttpRoutes[F] = httpErr.handle(HttpRoutes.of[F] {

    case req @ POST -> Root =>
      for {
        userAccount <- req.as[UserAccount]
        userInDb    <- userAccountService.insert(userAccount)
        resp        <- Ok(UserAccountResult(userInDb))
      } yield resp

    case req @ PUT -> Root / IntVar(id) =>
      for {
        userAccount <- req.as[UserAccount]
        userInDb    <- userAccountService.update(id, userAccount)
        resp        <- Ok(UserAccountResult(userInDb))
      } yield resp

    case DELETE -> Root / IntVar(id) =>
      for {
        userDelete  <- userAccountService.delete(id)
        resp        <- Ok(UserAccountResult(userDelete))
      } yield resp

    case req @ POST -> Root / "login" =>
      for {
        loginReq  <- req.as[LoginRequestPayload]
        userInDb  <- userAccountService.login(loginReq)
        resp      <- userInDb.fold(NotFound())(Ok(_))
      } yield resp
  })

}
