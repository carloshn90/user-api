package repository

import model.UserAccount

trait UserAccountRepository[F[_]] {

  def findByEmailAndPassword(email: String, password: String): F[Option[UserAccount]]
  def findById(id: Long): F[Option[UserAccount]]
  def insert(user: UserAccount): F[Int]
  def update(id: Long, user: UserAccount): F[Int]
  def delete(id: Long): F[Int]

}
