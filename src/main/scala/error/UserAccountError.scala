package error

import model.UserAccountModel

sealed trait UserAccountError extends Exception
case class InvalidUserAccount(userAccount: UserAccountModel, msg: String) extends UserAccountError
