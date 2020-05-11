package com.carher.service

import cats.data.{EitherT, OptionT}
import cats.effect.Async
import com.carher.authentication.AuthenticationService
import com.carher.model.UserAccountModel
import com.carher.payload.{JwtUserPayload, LoginRequestPayload, UserAccountPayload}
import com.carher.repository.UserAccountRepository
import com.carher.validation.ValidationUtil

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

  override def insert(user: UserAccountPayload): F[Either[List[String], Long]] = {

    val insertResultEitherT: EitherT[F, List[String], Long]= for {
      validUserPayload  <- EitherT.fromEither(validate(user))
      userModel         <- EitherT.rightT(UserAccountModel.fromUserAccountPayload(validUserPayload))
      result            <- EitherT(userAccountRep.insert(userModel)).leftMap(List(_))
    } yield result

    insertResultEitherT.value
  }

  override def update(id: Long, user: UserAccountPayload): F[Either[List[String], Long]] = {

    val updateResultEitherT: EitherT[F, List[String], Long] = for {
      validUserPayload     <- EitherT.fromEither(validate(user))
      userModel            <- EitherT.rightT(UserAccountModel.fromUserAccountPayload(validUserPayload))
      result               <- EitherT(userAccountRep.update(id, userModel)).leftMap(List(_))
    } yield result

    updateResultEitherT.value
  }

  override def delete(id: Long): F[Int] = userAccountRep.delete(id)

  private def validate(userPayload: UserAccountPayload): Either[List[String], UserAccountPayload] =
    UserAccountPayload.validate(userPayload)
      .toEither
      .left
      .map(ValidationUtil.getValidationErrors)
}
