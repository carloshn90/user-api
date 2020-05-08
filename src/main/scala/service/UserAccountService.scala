package service

import cats.data.NonEmptyChain
import payload.{LoginRequestPayload, UserAccountPayload}
import validation.UserAccountValidation

trait UserAccountService[F[_]] {

  def login(loginReq: LoginRequestPayload): F[Option[String]]
  def findById(id: Long): F[Option[UserAccountPayload]]
  def insert(user: UserAccountPayload): F[Either[NonEmptyChain[UserAccountValidation], Int]]
  def update(id: Long, user: UserAccountPayload): F[Either[NonEmptyChain[UserAccountValidation], Int]]
  def delete(id: Long): F[Int]
}
