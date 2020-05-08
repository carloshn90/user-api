package model

import payload.UserAccountPayload

final case class UserAccountModel
(
  id: Option[Long],
  name: String,
  surname: String,
  nickname: String,
  email: String,
  password: String
)

object UserAccountModel {

  def fromUserAccountPayload(userPayload: UserAccountPayload): UserAccountModel =
    UserAccountModel(None, userPayload.name, userPayload.surname, userPayload.nickname, userPayload.email, userPayload.password)
}