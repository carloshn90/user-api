package com.carher.payload

import cats.data.{Chain, NonEmptyChain}
import com.carher.UnitSpec
import com.carher.model.UserAccountModel
import com.carher.validation.{EmailDoesNotMeetCriteria, NameDoesNotMeetCriteria, PasswordDoesNotMeetCriteria, SurnameDoesNotMeetCriteria, UserAccountValidation, UsernameDoesNotMeetCriteria}

class UserAccountPayloadTest extends UnitSpec {

  "User account payload" should "created with the user account model values" in {
    val id = Some(1L)
    val name = "name-test"
    val surname = "surname-test"
    val username = "username-test"
    val email = "email-test"
    val password = "pass-test"
    val userModel: UserAccountModel = UserAccountModel(id, name, surname, username, email, password)

    val userPayload: UserAccountPayload = UserAccountPayload.fromUserAccountModel(userModel)

    userPayload.name shouldBe name
    userPayload.surname shouldBe surname
    userPayload.username shouldBe username
    userPayload.email shouldBe email
    userPayload.password shouldBe password
  }

  "Name validation" should "not be empty" in {
    val userPayload: UserAccountPayload = UserAccountPayload("", "surname", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(NameDoesNotMeetCriteria))
  }

  "Name validation" should "not contain space" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name name", "surname", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(NameDoesNotMeetCriteria))
  }

  "Name validation" should "not contain number" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name99", "surname", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(NameDoesNotMeetCriteria))
  }

  "Name validation" should "not contain special characters" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name@", "surname", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(NameDoesNotMeetCriteria))
  }

  "Name validation" should "be correct" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Right(userPayload)
  }

  "Surname validation" should "not be empty" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(SurnameDoesNotMeetCriteria))
  }

  "Surname validation" should "not contain space" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname surname", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(SurnameDoesNotMeetCriteria))
  }

  "Surname validation" should "not contain number" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname99", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(SurnameDoesNotMeetCriteria))
  }

  "Surname validation" should "not contain special characters" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname#", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(SurnameDoesNotMeetCriteria))
  }

  "Surname validation" should "be correct" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Right(userPayload)
  }

  "Username validation" should "not be empty" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(UsernameDoesNotMeetCriteria))
  }

  "Username validation" should "not contain space" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(UsernameDoesNotMeetCriteria))
  }

  "Username validation" should "not contain special characters" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username+", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(UsernameDoesNotMeetCriteria))
  }

  "Username validation" should "be correct" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username99", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Right(userPayload)
  }

  "Email validation" should "not be empty" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username", "", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(EmailDoesNotMeetCriteria))
  }

  "Email validation" should "not contain space" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username", "email@email.com email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(EmailDoesNotMeetCriteria))
  }

  "Email validation" should "contain @" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username", "email", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(EmailDoesNotMeetCriteria))
  }

  "Email validation" should "contain domain name " in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username", "email@email", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(EmailDoesNotMeetCriteria))
  }

  "Email validation" should "be correct" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Right(userPayload)
  }

  "Password validation" should "not be empty" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username", "email@email.com", "")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Left(Chain(PasswordDoesNotMeetCriteria))
  }

  "Password validation" should "be correct" in {
    val userPayload: UserAccountPayload = UserAccountPayload("name", "surname", "username", "email@email.com", "no-empty")
    val validResult: Either[NonEmptyChain[UserAccountValidation], UserAccountPayload] = UserAccountPayload.validate(userPayload).toEither

    validResult shouldBe Right(userPayload)
  }

}
