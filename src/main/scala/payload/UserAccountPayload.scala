package payload

import model.UserAccountModel

final case class UserAccountPayload
(
  name: String,
  surname: String,
  nickname: String,
  email: String
)

object UserAccountPayload {

  def userAccountModalToPayload(userModel: UserAccountModel): UserAccountPayload =
    UserAccountPayload(userModel.name, userModel.surname, userModel.nickname, userModel.email)
}
