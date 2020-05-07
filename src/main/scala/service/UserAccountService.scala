package service

import payload.{LoginRequestPayload, UserAccountPayload}

trait UserAccountService[F[_]] {

  def login(loginReq: LoginRequestPayload): F[Option[String]]
  def findById(id: Long): F[Option[UserAccountPayload]]
  def insert(user: UserAccountPayload): F[Int]
  def update(id: Long, user: UserAccountPayload): F[Int]
  def delete(id: Long): F[Int]
}
