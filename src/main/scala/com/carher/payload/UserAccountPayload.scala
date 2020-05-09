package com.carher.payload

import cats.data.ValidatedNec
import cats.implicits._
import com.carher.model.UserAccountModel
import com.carher.validation._

final case class UserAccountPayload
(
  name: String,
  surname: String,
  username: String,
  email: String,
  password: String
)

object UserAccountPayload {

  type ValidationResult[A] = ValidatedNec[UserAccountValidation, A]

  def fromUserAccountModel(userModel: UserAccountModel): UserAccountPayload =
    UserAccountPayload(userModel.name, userModel.surname, userModel.username, userModel.email, userModel.password)

  def validate(userPayload: UserAccountPayload): ValidationResult[UserAccountPayload] = (
      validateName(userPayload.name),
      validateSurname(userPayload.surname),
      validateUsername(userPayload.username),
      validateEmail(userPayload.email),
      validatePassword(userPayload.password)
    ).mapN(UserAccountPayload.apply)

  private def validateName(name: String): ValidationResult[String] =
    if (!name.isEmpty && name.matches("^[a-zA-Z]+$")) name.validNec else NameDoesNotMeetCriteria.invalidNec

  private def validateSurname(surname: String): ValidationResult[String] =
    if (!surname.isEmpty && surname.matches("^[a-zA-Z]+$")) surname.validNec else SurnameDoesNotMeetCriteria.invalidNec

  private def validateUsername(username: String): ValidationResult[String] =
    if (!username.isEmpty && username.matches("^[a-zA-Z0-9]+$")) username.validNec else UsernameDoesNotMeetCriteria.invalidNec

  private def validateEmail(email: String): ValidationResult[String] = {
    val emailMatcher = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)" +
      "*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?+$"
    if (!email.isEmpty && email.matches(emailMatcher)) email.validNec else EmailDoesNotMeetCriteria.invalidNec
  }

  private def validatePassword(password: String): ValidationResult[String] =
    if (!password.isEmpty && password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"))
      password.validNec
    else PasswordDoesNotMeetCriteria.invalidNec
}
