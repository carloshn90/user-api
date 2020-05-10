package com.carher.validation

import cats.data.NonEmptyChain
import cats.implicits._

sealed trait UserAccountValidation {
  def errorMessage: String
}

case object NameDoesNotMeetCriteria extends UserAccountValidation {
  @Override
  def errorMessage: String = "Name is mandatory and cannot contain spaces, numbers or special characters."
}

case object SurnameDoesNotMeetCriteria extends UserAccountValidation {
  @Override
  def errorMessage: String = "Surname is mandatory and cannot contain spaces, numbers or special characters."
}

case object UsernameDoesNotMeetCriteria extends UserAccountValidation {
  def errorMessage: String = "Username cannot contain special characters."
}

case object EmailDoesNotMeetCriteria extends UserAccountValidation {
  def errorMessage: String = "Email no valid"
}

case object PasswordDoesNotMeetCriteria extends UserAccountValidation {
  @Override
  def errorMessage: String = "Password must be at least 10 characters long, including an uppercase and a lowercase letter, one number and one special character."
}

object ValidationUtil {

  val emailMatcher: String = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)" +
    "*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?+$"

  val genericMatcher: String = "^[a-zA-Z]+$"

  val usernameMatcher: String = "^[a-zA-Z0-9]+$"

  def getValidationErrors(chain: NonEmptyChain[UserAccountValidation]): List[String] =
    chain.toList.map(err => err.errorMessage)

}


