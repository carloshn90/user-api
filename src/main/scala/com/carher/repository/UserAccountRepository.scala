package com.carher.repository

import com.carher.model.UserAccountModel

trait UserAccountRepository[F[_]] {

  def findByEmailAndPassword(email: String, password: String): F[Option[UserAccountModel]]
  def findById(id: Long): F[Option[UserAccountModel]]
  def insert(user: UserAccountModel): F[Int]
  def update(id: Long, user: UserAccountModel): F[Int]
  def delete(id: Long): F[Int]

}
