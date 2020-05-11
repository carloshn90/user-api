package com.carher.repository

import cats.effect.IO
import com.carher.UnitSpec
import com.carher.model.UserAccountModel
import com.softwaremill.macwire.wire
import doobie.util.transactor.Transactor
import doobie.implicits._

class UserAccountDoobieTest extends UnitSpec {

  private val transactor: Transactor[IO] = PostgresDbUtil.createPostgresTransactor

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    sql"""CREATE TABLE user_account (id SERIAL not null primary key, name varchar, surname varchar, username varchar,
         email varchar UNIQUE, password varchar)"""
      .update
      .run
      .transact(transactor)
      .unsafeRunSync
  }

  override protected def afterEach(): Unit = {
    super.beforeEach()

    sql"DELETE FROM user_account"
      .update
      .run
      .transact(transactor)
      .unsafeRunSync
  }

  override def afterAll(): Unit = {
    super.afterAll()

    sql"DROP TABLE user_account"
      .update
      .run
      .transact(transactor)
      .unsafeRunSync
  }

  "Find by email and password user account" should "get an user account from the database" in {
    val email: String = "email-test.com"
    val password: String = "password-test"
    val userInsert: UserAccountModel = UserAccountModel(None, "name-test", "surname-test", "username-test", email, password)
    val userAccountDoobie: UserAccountDoobie[IO] = wire[UserAccountDoobie[IO]]

    userAccountDoobie.insert(userInsert)
      .unsafeRunSync

    val userInsertedFromDb: Option[UserAccountModel] = userAccountDoobie.findByEmailAndPassword(email, password)
      .unsafeRunSync
    val userNoExistInDb: Option[UserAccountModel] = userAccountDoobie.findByEmailAndPassword("email-no-exist", "password-no-exist")
      .unsafeRunSync

    userInsertedFromDb.isDefined shouldBe true
    userNoExistInDb.isDefined shouldBe false
  }

  "Find by id" should "get an user account from the database" in {
    val userInsert: UserAccountModel = UserAccountModel(None, "name-test", "surname-test", "username-test", "email-test.com", "password-test")
    val userAccountDoobie: UserAccountDoobie[IO] = wire[UserAccountDoobie[IO]]

    val userIdFromDb: Either[String, Long] = userAccountDoobie.insert(userInsert)
      .unsafeRunSync

    val idInsertedFromDb: Long = userIdFromDb match {
      case Right(value) => value
      case Left(ex)     => fail(ex)
    }

    val userInsertedFromDb: Option[UserAccountModel] = userAccountDoobie.findById(idInsertedFromDb)
      .unsafeRunSync

    userInsertedFromDb.isDefined shouldBe true
  }

  "Insert user account" should "saved the user account in the database" in {
    val userAccountModel: UserAccountModel = UserAccountModel(None, "name-test", "surname-test", "username-test", "email-test.com", "password-test")
    val userAccountDoobie: UserAccountDoobie[IO] = wire[UserAccountDoobie[IO]]

    val userIdFromDb: Either[String, Long] = userAccountDoobie.insert(userAccountModel)
      .unsafeRunSync

    userIdFromDb.isRight shouldBe true
  }

  "Insert duplicate user account" should "return unique error" in {
    val userAccountModel: UserAccountModel = UserAccountModel(None, "name-test", "surname-test", "username-test", "email-test.com", "password-test")
    val userAccountDoobie: UserAccountDoobie[IO] = wire[UserAccountDoobie[IO]]

    val userIdFromDbCorrect: Either[String, Long] = userAccountDoobie.insert(userAccountModel)
      .unsafeRunSync

    val userIdFromDbFail: Either[String, Long] = userAccountDoobie.insert(userAccountModel)
      .unsafeRunSync

    userIdFromDbCorrect.isRight shouldBe true
    userIdFromDbFail.isLeft shouldBe true
  }

  "Update user account email" should "updated the user account email in the database" in {
    val emailUpdate: String = "email-test-updated.com"
    val userInsert: UserAccountModel = UserAccountModel(None, "name-test", "surname-test", "username-test", "email-test.com", "password-test")
    val userUpdate: UserAccountModel = UserAccountModel(None, "name-test", "surname-test", "username-test", emailUpdate, "password-test")
    val userAccountDoobie: UserAccountDoobie[IO] = wire[UserAccountDoobie[IO]]

    val userIdFromDb: Either[String, Long] = userAccountDoobie.insert(userInsert)
      .unsafeRunSync

    val idFromDb: Long = userIdFromDb match {
      case Right(value) => value
      case Left(ex)     => fail(ex)
    }

    userAccountDoobie.update(idFromDb,userUpdate)
      .unsafeRunSync

    val userUpdatedFromDb: Option[UserAccountModel] = userAccountDoobie.findById(idFromDb)
      .unsafeRunSync

    userUpdatedFromDb.isDefined shouldBe true
    userUpdatedFromDb.get.email shouldBe emailUpdate
  }

  "Update user account no exist" should "return error message" in {
    val idNoExistDb: Long = 1
    val userModel: UserAccountModel = UserAccountModel(None, "name-test", "surname-test", "username-test", "email-test.com", "password-test")
    val userAccountDoobie: UserAccountDoobie[IO] = wire[UserAccountDoobie[IO]]

    val userIdFromDb: Either[String, Long] = userAccountDoobie.update(idNoExistDb,userModel)
      .unsafeRunSync

    userIdFromDb.isLeft shouldBe true
  }

  "Delete user account" should "deleted the user account" in {
    val userInsert: UserAccountModel = UserAccountModel(None, "name-test", "surname-test", "username-test", "email-test.com", "password-test")
    val userAccountDoobie: UserAccountDoobie[IO] = wire[UserAccountDoobie[IO]]

    val userIdFromDb: Either[String, Long] = userAccountDoobie.insert(userInsert)
      .unsafeRunSync

    val idInsertedFromDb: Long = userIdFromDb match {
      case Right(value) => value
      case Left(ex)     => fail(ex)
    }

    userAccountDoobie.delete(idInsertedFromDb)
      .unsafeRunSync
    val userDeletedFromDb: Option[UserAccountModel] = userAccountDoobie.findById(idInsertedFromDb)
      .unsafeRunSync

    userDeletedFromDb.isDefined shouldBe false
  }

  "Delete user account no exist" should "return 0" in {
    val userAccountDoobie: UserAccountDoobie[IO] = wire[UserAccountDoobie[IO]]

    val numberOfDeletedUser: Int = userAccountDoobie.delete(2L)
      .unsafeRunSync

    numberOfDeletedUser shouldBe 0
  }
}
