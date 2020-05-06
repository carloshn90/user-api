package service

import model.UserAccount
import payload.LoginRequestPayload

trait UserAccountService[F[_]] {

  def login(loginReq: LoginRequestPayload): F[Option[UserAccount]]
  def select(id: Int): F[Option[UserAccount]]
  def insert(user: UserAccount): F[Int]
  def update(id: Int, user: UserAccount): F[Int]
  def delete(id: Int): F[Int]
}
