package repository

import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor
import model.UserAccount

import scala.collection.mutable

import repository.UserAccountDoobie._

class UserAccountDoobie[F[_]: Sync](xa: Transactor[F]) extends UserAccountRepository[F] {

  implicit val han: LogHandler = LogHandler.jdkLogHandler

  override def findByEmailAndPassword(email: String, password: String): F[Option[UserAccount]] =
    sql"SELECT * FROM user_account WHERE email = $email AND password = $password"
      .query[UserAccount]
      .to[List]
      .map(_.headOption)
      .transact(xa)

  override def select(id: Int): F[Option[UserAccount]] =
    sql"SELECT * FROM user_account"
      .query[UserAccount]
      .to[List]
      .map(_.headOption)
      .transact(xa)

  override def insert(user: UserAccount): F[Int] = {
    val userAccountFrag =
      fr"VALUES (${user.id}, ${user.name}, ${user.surname}, ${user.nickname}, ${user.email}, ${user.password})"

    (insertUserAccountFrag ++ userAccountFrag).update.run.transact(xa)
  }

  override def update(id: Int, user: UserAccount): F[Int] = {
    val userAccountFrag =
      fr"(${user.id}, ${user.name}, ${user.surname}, ${user.nickname}, ${user.email}, ${user.password})"

    (updateUserAccountFrag ++ userAccountFrag).update.run.transact(xa)
  }

  override def delete(id: Int): F[Int] = sql"DELETE FROM user_account WHERE id = $id".update.run.transact(xa)
}

object UserAccountDoobie {

  val (columns, columnsWithComma) = {
    val columns = mutable.LinkedHashSet[String]("id", "name", "username", "nickname", "email", "password")
    (columns.toSet, columns.mkString(","))
  }

  val insertUserAccountFrag: Fragment = fr"INSERT INTO user_account (" ++ Fragment.const(columnsWithComma) ++ fr")"
  val updateUserAccountFrag: Fragment = fr"UPDATE user_account SET (" ++ Fragment.const(columnsWithComma) ++ fr") = "
}
