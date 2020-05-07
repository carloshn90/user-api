package service

import model.UserAccount
import payload.LoginRequestPayload

trait UserAccountService[F[_]] {

  def login(loginReq: LoginRequestPayload): F[Option[String]]
  def findById(id: Long): F[Option[UserAccount]]
  def insert(user: UserAccount): F[Int]
  def update(id: Long, user: UserAccount): F[Int]
  def delete(id: Long): F[Int]
}
