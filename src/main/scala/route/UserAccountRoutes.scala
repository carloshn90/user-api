package route

import cats.effect.Sync
import cats.implicits._
import error.{HttpErrorHandler, UserAccountError}
import json.CirceJsonCodecs
import model.{UserAccount, UserAccountResult}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import service.UserAccountService

class UserAccountRoutes[F[_]: Sync](userAccountService: UserAccountService[F])
                                   (implicit httpErr: HttpErrorHandler[F, UserAccountError]) extends Http4sDsl[F] with CirceJsonCodecs {

  val routes: HttpRoutes[F] = httpErr.handle(HttpRoutes.of[F] {
    case GET -> Root / IntVar(id) =>
      for {
        userDb <- userAccountService.select(id)
      } yield userDb match {
        case Some(userAccount)  => Ok(userAccount)
        case None               => NotFound()
      }

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
  })

}
