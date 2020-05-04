package repository

import model.UserAccount

trait UserAccountRepository[F[_]] {

  def findByEmailAndPassword(email: String, password: String): F[Option[UserAccount]]
  def select(id: Int): F[Option[UserAccount]]
  def insert(user: UserAccount): F[Int]
  def update(id: Int, user: UserAccount): F[Int]
  def delete(id: Int): F[Int]

}
