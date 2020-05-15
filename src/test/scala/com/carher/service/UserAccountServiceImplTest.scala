package com.carher.service

import cats.effect.IO
import cats.implicits._
import com.carher.UnitSpec
import com.carher.authentication.AuthenticationService
import com.carher.model.UserAccountModel
import com.carher.payload.{LoginRequestPayload, UserAccountPayload}
import com.carher.repository.UserAccountRepository
import com.carher.validation.EmailDoesNotMeetCriteria

class UserAccountServiceImplTest extends UnitSpec {

  "Login user not in the database" should "return None" in {
    val userNotFound: Option[UserAccountModel] = None
    val userAccountRepositoryMock: UserAccountRepository[IO] = mock[UserAccountRepository[IO]]
    val authServiceMock: AuthenticationService = mock[AuthenticationService]
    val userAccountService: UserAccountServiceImpl[IO] = new UserAccountServiceImpl[IO](userAccountRepositoryMock, authServiceMock)

    (userAccountRepositoryMock.findByEmailAndPassword _).expects(*, *).returning(IO.pure(userNotFound)).once()

    val result: Option[String] = userAccountService.login(LoginRequestPayload("email", "password"))
      .unsafeRunSync

    result shouldBe None
  }

  "Login user in the database" should "return a token" in {
    val userInDb: UserAccountModel = UserAccountModel(Some(1L), "nam", "sur", "user", "email", "pass")
    val token: Option[String] = Some("token-test")
    val userFound: Option[UserAccountModel] = Some(userInDb)
    val userAccountRepositoryMock: UserAccountRepository[IO] = mock[UserAccountRepository[IO]]
    val authServiceMock: AuthenticationService = mock[AuthenticationService]
    val userAccountService: UserAccountServiceImpl[IO] = new UserAccountServiceImpl[IO](userAccountRepositoryMock, authServiceMock)

    (userAccountRepositoryMock.findByEmailAndPassword _).expects(*, *).returning(IO.pure(userFound)).once()
    (authServiceMock.encodeToken _).expects(*).returning(token).once()

    val result: Option[String] = userAccountService.login(LoginRequestPayload("email", "password"))
      .unsafeRunSync

    result shouldBe token
  }

  "FindById user not in the database" should "return None" in {
    val userId: Long = 1L
    val userNoInDb: Option[UserAccountModel] = None
    val userAccountRepositoryMock: UserAccountRepository[IO] = mock[UserAccountRepository[IO]]
    val authServiceMock: AuthenticationService = mock[AuthenticationService]
    val userAccountService: UserAccountServiceImpl[IO] = new UserAccountServiceImpl[IO](userAccountRepositoryMock, authServiceMock)

    (userAccountRepositoryMock.findById _).expects(userId).returning(IO.pure(userNoInDb)).once()

    val result: Option[UserAccountPayload] = userAccountService.findById(userId)
      .unsafeRunSync

    result shouldBe None
  }

  "FindById user in the database" should "return user from the database" in {
    val userId: Long = 1L
    val userInDb: Option[UserAccountModel] = Some(UserAccountModel(Some(1L), "nam", "sur", "user", "email", "pass"))
    val expectedUserPayload: Option[UserAccountPayload] = Some(UserAccountPayload("nam", "sur", "user", "email", "pass"))
    val userAccountRepositoryMock: UserAccountRepository[IO] = mock[UserAccountRepository[IO]]
    val authServiceMock: AuthenticationService = mock[AuthenticationService]
    val userAccountService: UserAccountServiceImpl[IO] = new UserAccountServiceImpl[IO](userAccountRepositoryMock, authServiceMock)

    (userAccountRepositoryMock.findById _).expects(userId).returning(IO.pure(userInDb)).once()

    val result: Option[UserAccountPayload] = userAccountService.findById(userId)
      .unsafeRunSync

    result shouldBe expectedUserPayload
  }

  "Insert user in the database email validation error" should "return error from the validation" in {
    val userPayloadNotValid: UserAccountPayload = UserAccountPayload("nam", "sur", "user", "email", "pass")
    val userAccountRepositoryMock: UserAccountRepository[IO] = mock[UserAccountRepository[IO]]
    val authServiceMock: AuthenticationService = mock[AuthenticationService]
    val userAccountService: UserAccountServiceImpl[IO] = new UserAccountServiceImpl[IO](userAccountRepositoryMock, authServiceMock)

    val result: Either[List[String], Long] = userAccountService.insert(userPayloadNotValid)
      .unsafeRunSync

    result match {
      case Left(values) => values shouldBe List(EmailDoesNotMeetCriteria.errorMessage)
      case _            => fail("Insert test error")
    }
  }

  "Insert user in the database, error from the repository" should "return error from database" in {
    val userPayloadValid: UserAccountPayload = UserAccountPayload("nam", "sur", "user", "email@email.com", "pass")
    val errorMsg: String = "Db error!"
    val dbError: Either[String, Long] = Either.left[String, Long](errorMsg)
    val userAccountRepositoryMock: UserAccountRepository[IO] = mock[UserAccountRepository[IO]]
    val authServiceMock: AuthenticationService = mock[AuthenticationService]
    val userAccountService: UserAccountServiceImpl[IO] = new UserAccountServiceImpl[IO](userAccountRepositoryMock, authServiceMock)

    (userAccountRepositoryMock.insert _).expects(*).returning(IO.pure(dbError)).once()

    val result: Either[List[String], Long] = userAccountService.insert(userPayloadValid)
      .unsafeRunSync

    result match {
      case Left(values) => values shouldBe List(errorMsg)
      case _            => fail("Insert test error")
    }
  }

  "Insert user in the database, correct" should "return user id" in {
    val userPayloadValid: UserAccountPayload = UserAccountPayload("nam", "sur", "user", "email@email.com", "pass")
    val idFromDb: Long = 2L
    val dbSuccess: Either[String, Long] = Either.right[String, Long](idFromDb)
    val userAccountRepositoryMock: UserAccountRepository[IO] = mock[UserAccountRepository[IO]]
    val authServiceMock: AuthenticationService = mock[AuthenticationService]
    val userAccountService: UserAccountServiceImpl[IO] = new UserAccountServiceImpl[IO](userAccountRepositoryMock, authServiceMock)

    (userAccountRepositoryMock.insert _).expects(*).returning(IO.pure(dbSuccess)).once()

    val result: Either[List[String], Long] = userAccountService.insert(userPayloadValid)
      .unsafeRunSync

    result match {
      case Right(value) => value shouldBe idFromDb
      case _            => fail("Insert test error")
    }
  }

  "Update user in the database email validation error" should "return error from the validation" in {
    val userPayloadNotValid: UserAccountPayload = UserAccountPayload("nam", "sur", "user", "email", "pass")
    val userAccountRepositoryMock: UserAccountRepository[IO] = mock[UserAccountRepository[IO]]
    val authServiceMock: AuthenticationService = mock[AuthenticationService]
    val userAccountService: UserAccountServiceImpl[IO] = new UserAccountServiceImpl[IO](userAccountRepositoryMock, authServiceMock)

    val result: Either[List[String], Long] = userAccountService.update(1L, userPayloadNotValid)
      .unsafeRunSync

    result match {
      case Left(values) => values shouldBe List(EmailDoesNotMeetCriteria.errorMessage)
      case _            => fail("Update test error")
    }
  }

  "Update user in the database, error from the repository" should "return error from database" in {
    val userPayloadValid: UserAccountPayload = UserAccountPayload("nam", "sur", "user", "email@email.com", "pass")
    val userId: Long = 2L
    val errorMsg: String = "Db error!"
    val dbError: Either[String, Long] = Either.left[String, Long](errorMsg)
    val userAccountRepositoryMock: UserAccountRepository[IO] = mock[UserAccountRepository[IO]]
    val authServiceMock: AuthenticationService = mock[AuthenticationService]
    val userAccountService: UserAccountServiceImpl[IO] = new UserAccountServiceImpl[IO](userAccountRepositoryMock, authServiceMock)

    (userAccountRepositoryMock.update _).expects(userId, *).returning(IO.pure(dbError)).once()

    val result: Either[List[String], Long] = userAccountService.update(userId, userPayloadValid)
      .unsafeRunSync

    result match {
      case Left(values) => values shouldBe List(errorMsg)
      case _            => fail("Update test error")
    }
  }

  "Update user in the database, correct" should "return user id" in {
    val userPayloadValid: UserAccountPayload = UserAccountPayload("nam", "sur", "user", "email@email.com", "pass")
    val userId: Long = 2L
    val idFromDb: Long = 2L
    val dbSuccess: Either[String, Long] = Either.right[String, Long](idFromDb)
    val userAccountRepositoryMock: UserAccountRepository[IO] = mock[UserAccountRepository[IO]]
    val authServiceMock: AuthenticationService = mock[AuthenticationService]
    val userAccountService: UserAccountServiceImpl[IO] = new UserAccountServiceImpl[IO](userAccountRepositoryMock, authServiceMock)

    (userAccountRepositoryMock.update _).expects(userId, *).returning(IO.pure(dbSuccess)).once()

    val result: Either[List[String], Long] = userAccountService.update(userId, userPayloadValid)
      .unsafeRunSync

    result match {
      case Right(value) => value shouldBe idFromDb
      case _            => fail("Update test error")
    }
  }

  "Delete user in the database, correct" should "return number of rows deleted" in {
    val userIdToDelete: Long = 2L
    val userAccountRepositoryMock: UserAccountRepository[IO] = mock[UserAccountRepository[IO]]
    val authServiceMock: AuthenticationService = mock[AuthenticationService]
    val userAccountService: UserAccountServiceImpl[IO] = new UserAccountServiceImpl[IO](userAccountRepositoryMock, authServiceMock)

    (userAccountRepositoryMock.delete _).expects(userIdToDelete).returning(IO.pure(1)).once()

    val result: Int = userAccountService.delete(userIdToDelete)
      .unsafeRunSync

    result shouldBe 1
  }

}
