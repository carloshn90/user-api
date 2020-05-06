package authentication

import java.time.Clock

import model.UserAccount
import io.circe.syntax._
import io.circe.generic.auto._
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import scala.util.{Failure, Success}

object AuthenticationService {

  implicit val clock: Clock = Clock.systemUTC
  val defaultKey = "key"

  def encodeToken(user: UserAccount, key: String = defaultKey): String =
    JwtCirce.encode(user.asJson, key, JwtAlgorithm.HS256)

  def decodeToken(token: String, key: String = defaultKey): Option[JwtClaim] = {
    JwtCirce.decode(token, key, Seq(JwtAlgorithm.HS256)) match {
      case Success(jwtUserJson) => Some(jwtUserJson)
      case Failure(_) => None
    }
  }
}
