package error

import model.UserAccount

sealed trait UserAccountError extends Exception
case class InvalidUserAccount(userAccount: UserAccount, msg: String) extends UserAccountError
