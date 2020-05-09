package com.carher.model

import com.carher.payload.UserAccountPayload

final case class UserAccountModel
(
  id: Option[Long],
  name: String,
  surname: String,
  username: String,
  email: String,
  password: String
)

object UserAccountModel {

  def fromUserAccountPayload(userPayload: UserAccountPayload): UserAccountModel =
    UserAccountModel(None, userPayload.name, userPayload.surname, userPayload.username, userPayload.email, userPayload.password)
}