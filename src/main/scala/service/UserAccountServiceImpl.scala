package service

import authentication.AuthenticationService
import cats.data.OptionT
import cats.effect.Async
import model.UserAccountModel
import payload.{JwtUserPayload, LoginRequestPayload, UserAccountPayload}
import repository.UserAccountRepository

class UserAccountServiceImpl[F[_]: Async](userAccountRep: UserAccountRepository[F],
                                          authService: AuthenticationService) extends UserAccountService[F]{

  override def login(loginReq: LoginRequestPayload): F[Option[String]] = loginOptionT(loginReq).value

  override def findById(id: Long): F[Option[UserAccountPayload]] = findIdOptionT(id).value

  override def insert(user: UserAccountPayload): F[Int] = userAccountRep.insert(UserAccountModel.userPayloadToModel(user))

  override def update(id: Long, user: UserAccountPayload): F[Int] = userAccountRep.update(id, UserAccountModel.userPayloadToModel(user))

  override def delete(id: Long): F[Int] = userAccountRep.delete(id)

  private def loginOptionT(loginReq: LoginRequestPayload): OptionT[F, String] = for {
    userAccountDb <- OptionT(userAccountRep.findByEmailAndPassword(loginReq.email, loginReq.password))
    jwtUser       <- OptionT.fromOption(JwtUserPayload.userAccountModelToJwtUserPayload(userAccountDb))
    jwtToken      <- OptionT.fromOption(authService.encodeToken(jwtUser))
  } yield jwtToken

  private def findIdOptionT(id: Long): OptionT[F, UserAccountPayload] = for {
    userAccountDb <- OptionT(userAccountRep.findById(id))
    userAccountPayload <- OptionT.some(UserAccountPayload.userAccountModalToPayload(userAccountDb))
  } yield userAccountPayload

}
