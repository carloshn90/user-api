package service

import authentication.AuthenticationService
import cats.data.{IdT, OptionT}
import cats.effect.Async
import model.UserAccountModel
import payload.{JwtUserPayload, LoginRequestPayload, UserAccountPayload}
import repository.UserAccountRepository

class UserAccountServiceImpl[F[_]: Async](userAccountRep: UserAccountRepository[F],
                                          authService: AuthenticationService) extends UserAccountService[F]{

  override def login(loginReq: LoginRequestPayload): F[Option[String]] = {

    val tokenOptionT: OptionT[F, String] = for {
      userAccountDb <- OptionT(userAccountRep.findByEmailAndPassword(loginReq.email, loginReq.password))
      jwtUser       <- OptionT.fromOption(JwtUserPayload.fromUserAccountModel(userAccountDb))
      jwtToken      <- OptionT.fromOption(authService.encodeToken(jwtUser))
    } yield jwtToken

    tokenOptionT.value
  }

  override def findById(id: Long): F[Option[UserAccountPayload]] = {

    val findByIdOptionT: OptionT[F, UserAccountPayload] = for {
      userAccountDb       <- OptionT(userAccountRep.findById(id))
      userAccountPayload  <- OptionT.some(UserAccountPayload.fromUserAccountModel(userAccountDb))
    } yield userAccountPayload

    findByIdOptionT.value
  }

  override def insert(user: UserAccountPayload): F[Int] = {

    val insertResultIdT: IdT[F, Int] = for {
      userModel <- IdT.pure(UserAccountModel.fromUserAccountPayload(user))
      result    <- IdT(userAccountRep.insert(userModel))
    } yield result

    insertResultIdT.value
  }

  override def update(id: Long, user: UserAccountPayload): F[Int] = {

    val updateResultIdT: IdT[F, Int] = for {
      userModel   <- IdT.pure(UserAccountModel.fromUserAccountPayload(user))
      result      <- IdT(userAccountRep.update(id, userModel))
    } yield result

    updateResultIdT.value
  }

  override def delete(id: Long): F[Int] = userAccountRep.delete(id)

}
