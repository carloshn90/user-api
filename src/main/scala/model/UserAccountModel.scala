package model

import payload.UserAccountPayload

final case class UserAccountModel
(
  id: Option[Long],
  name: String,
  surname: String,
  nickname: String,
  email: String,
  password: Option[String]
)

object UserAccountModel {
  def userPayloadToModel(userPayload: UserAccountPayload): UserAccountModel =
    UserAccountModel(None, userPayload.name, userPayload.surname, userPayload.nickname, userPayload.email, None)
}