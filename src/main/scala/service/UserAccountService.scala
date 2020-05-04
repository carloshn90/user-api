package service

import model.UserAccount

trait UserAccountService[F[_]] {

  def login(email: String, password: String): F[Option[UserAccount]]
  def select(id: Int): F[Option[UserAccount]]
  def insert(user: UserAccount): F[Int]
  def update(id: Int, user: UserAccount): F[Int]
  def delete(id: Int): F[Int]
}
