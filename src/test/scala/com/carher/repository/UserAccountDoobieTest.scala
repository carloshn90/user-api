package com.carher.repository

import cats.effect.{Blocker, ContextShift, IO}
import com.carher.UnitSpec
import com.carher.model.UserAccountModel
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.softwaremill.macwire.wire
import doobie.util.transactor.Transactor
import doobie.implicits._

import scala.concurrent.ExecutionContext

class UserAccountDoobieTest extends UnitSpec {

  private var postgres: EmbeddedPostgres = _
  private var transactor: Transactor[IO] = _

  implicit private val ioContextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    postgres = EmbeddedPostgres.builder().start()
    transactor = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      postgres.getJdbcUrl("postgres", "postgres"),
      "postgres",
      "postgres",
      Blocker.liftExecutionContext(ExecutionContext.global)
    )

    sql"""CREATE TABLE user_account (id SERIAL not null primary key, name varchar, surname varchar, username varchar,
         email varchar, password varchar)"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync()
  }

  override protected def afterEach(): Unit = {
    super.beforeEach()

    sql"DELETE FROM user_account"
      .update
      .run
      .transact(transactor)
      .unsafeRunSync()
  }

  override def afterAll(): Unit = {
    super.afterAll()

    sql"DROP TABLE user_account"
      .update
      .run
      .transact(transactor)
      .unsafeRunSync()
  }

  "Insert user account with id None" should "saved the user account in the database" in {
    val email: String = "email-test.com"
    val password: String = "password-test"
    val userAccountModel: UserAccountModel = UserAccountModel(None, "name-test", "surname-test", "username-test", email, password)
    val userAccountDoobie: UserAccountDoobie[IO] = wire[UserAccountDoobie[IO]]

    userAccountDoobie.insert(userAccountModel).unsafeRunSync()

    val userFromDb = userAccountDoobie.findByEmailAndPassword(email, password).unsafeRunSync()
    userFromDb.isDefined shouldBe true
  }
}
