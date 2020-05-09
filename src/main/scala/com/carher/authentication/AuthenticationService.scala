package com.carher.authentication

import java.time.{Clock, Instant}

import com.carher.config.JwtConfig
import com.carher.json.CirceJsonCodecs
import com.carher.payload.JwtUserPayload
import io.circe.Error
import io.circe.parser.decode
import io.circe.syntax._
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import scala.util.{Failure, Success}

class AuthenticationService(jwtConfig: JwtConfig) extends CirceJsonCodecs {

  implicit val clock: Clock = Clock.systemUTC

  def createClaim(jwtUser: JwtUserPayload): JwtClaim = JwtClaim(
    expiration = Some(Instant.now.plusSeconds(jwtConfig.expirationSeconds).getEpochSecond),
    issuedAt = Some(Instant.now.getEpochSecond),
    content = jwtUser.asJson.noSpaces
  )

  def encodeToken(jwtUser: JwtUserPayload): Option[String] = for {
    claim <- Some(createClaim(jwtUser))
    token <- Some(JwtCirce.encode(claim, jwtConfig.password, JwtAlgorithm.HS256))
  } yield token

  def decodeToken(token: String): Either[String, JwtUserPayload] = {
    JwtCirce.decode(token, jwtConfig.password, Seq(JwtAlgorithm.HS256)) match {
      case Success(jwtClaim) => getJwtUser(jwtClaim).left.map(_.getMessage)
      case Failure(_) => Left("Failed to decode Jwt")
    }
  }

  def getJwtUser(jwtClaim: JwtClaim): Either[Error, JwtUserPayload] = for {
      jwtUser <- decode[JwtUserPayload](jwtClaim.content)
    } yield jwtUser

  def getTokenFromHeader(header: String): String = header.replace(jwtConfig.prefix, "")

}
