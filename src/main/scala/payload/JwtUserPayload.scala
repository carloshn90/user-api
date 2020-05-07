package payload

import model.UserAccountModel

final case class JwtUserPayload(userId: Long)

object JwtUserPayload {
  def userAccountModelToJwtUserPayload(userModel: UserAccountModel): Option[JwtUserPayload] = for {
    userId <- userModel.id
    jwtPayload <- Option(JwtUserPayload(userId))
  } yield jwtPayload
}
