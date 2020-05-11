package com.carher.repository

import cats.effect.Sync
import cats.implicits._
import com.carher.model.UserAccountModel
import com.carher.repository.UserAccountDoobie._
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor

import scala.collection.mutable

class UserAccountDoobie[F[_]: Sync](xa: Transactor[F]) extends UserAccountRepository[F] {

  implicit val han: LogHandler = LogHandler.jdkLogHandler

  override def findByEmailAndPassword(email: String, password: String): F[Option[UserAccountModel]] =
    sql"SELECT * FROM user_account WHERE email = $email AND password = $password"
      .query[UserAccountModel]
      .to[List]
      .map(_.headOption)
      .transact(xa)

  override def findById(id: Long): F[Option[UserAccountModel]] =
    sql"SELECT * FROM user_account"
      .query[UserAccountModel]
      .to[List]
      .map(_.headOption)
      .transact(xa)

  override def insert(user: UserAccountModel): F[Either[String, Long]] = {
    val userAccountFrag =
      fr"VALUES (${user.name}, ${user.surname}, ${user.username}, ${user.email}, ${user.password})"

    (insertUserAccountFrag ++ userAccountFrag).update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(xa)
      .attemptSomeSqlState { case ex => ex.value }
      .attempt
      .map {
        case Left(ex) => Either.left[String, Long](ex.getMessage)
        case Right(value) => value
      }
  }

  override def update(id: Long, user: UserAccountModel): F[Either[String, Long]] = {

    val userAccountFrag =
      fr"(${user.name}, ${user.surname}, ${user.username}, ${user.email}, ${user.password})"

    (updateUserAccountFrag ++ userAccountFrag ++ whereIdEqualFrag ++ fr"$id").update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(xa)
      .attemptSomeSqlState { case ex => ex.value}
      .attempt
      .map {
        case Left(ex) => Either.left[String, Long](ex.getMessage)
        case Right(value) => value
      }
  }

  override def delete(id: Long): F[Int] = sql"DELETE FROM user_account WHERE id = $id".update.run.transact(xa)
}

object UserAccountDoobie {

  val (columns, columnsWithComma) = {
    val columns = mutable.LinkedHashSet[String]("name", "surname", "username", "email", "password")
    (columns.toSet, columns.mkString(","))
  }

  val insertUserAccountFrag: Fragment = fr"INSERT INTO user_account (" ++ Fragment.const(columnsWithComma) ++ fr")"
  val updateUserAccountFrag: Fragment = fr"UPDATE user_account SET (" ++ Fragment.const(columnsWithComma) ++ fr") = "
  val whereIdEqualFrag: Fragment = fr"WHERE id = "
}
