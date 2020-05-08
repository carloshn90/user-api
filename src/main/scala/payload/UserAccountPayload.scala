package payload

import model.UserAccountModel

final case class UserAccountPayload
(
  name: String,
  surname: String,
  nickname: String,
  email: String,
  password: String
)

object UserAccountPayload {

  def fromUserAccountModel(userModel: UserAccountModel): UserAccountPayload =
    UserAccountPayload(userModel.name, userModel.surname, userModel.nickname, userModel.email, userModel.password)
}
