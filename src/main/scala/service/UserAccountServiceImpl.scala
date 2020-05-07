package service

import authentication.AuthenticationService
import cats.data.OptionT
import cats.effect.Async
import model.UserAccount
import payload.{JwtUserPayload, LoginRequestPayload}
import repository.UserAccountRepository

class UserAccountServiceImpl[F[_]: Async](userAccountRep: UserAccountRepository[F],
                                          authService: AuthenticationService) extends UserAccountService[F]{


  override def login(loginReq: LoginRequestPayload): F[Option[String]] = {

    val jwtTokenOptionT: OptionT[F, String] = for {
      userAccountDb <- OptionT(userAccountRep.findByEmailAndPassword(loginReq.email, loginReq.password))
      jwtUser       <- OptionT.some(JwtUserPayload(userAccountDb.id))
      jwtToken      <- OptionT.fromOption(authService.encodeToken(jwtUser))
    } yield jwtToken

    jwtTokenOptionT.value
  }

  override def findById(id: Long): F[Option[UserAccount]] = userAccountRep.findById(id)

  override def insert(user: UserAccount): F[Int] = userAccountRep.insert(user)

  override def update(id: Long, user: UserAccount): F[Int] = userAccountRep.update(id, user)

  override def delete(id: Long): F[Int] = userAccountRep.delete(id)
}
