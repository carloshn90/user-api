package com.carher.authentication

import com.carher.UnitSpec
import com.carher.config.JwtConfig
import com.carher.payload.JwtUserPayload
import com.softwaremill.macwire.wire

class AuthenticationServiceTest extends UnitSpec {

  val jwtConfig: JwtConfig = JwtConfig(password = "test", prefix = "Bearer", expirationSeconds = 60)
  val authenticationService: AuthenticationService = wire[AuthenticationService]

  "Encode token with JwtUserPayload" should "return a token string" in {
    val token: Option[String] = authenticationService.encodeToken(JwtUserPayload(1))

    token.isDefined shouldBe true
  }

  "Check encode token with the decode" should "correct validation process" in {
    val jwtUserPayload: JwtUserPayload = JwtUserPayload(1)

    val decodedToken: Either[String, JwtUserPayload] = for {
      token   <- authenticationService.encodeToken(jwtUserPayload).toRight("Error in the token creation")
      result  <- authenticationService.decodeToken(token)
    } yield result

    decodedToken shouldBe Right(jwtUserPayload)
  }

  "Decode invalid token" should "fail validation with error" in {
    val invalidToken: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"

    val decodeToken: Either[String, JwtUserPayload] = authenticationService.decodeToken(invalidToken)

    decodeToken shouldBe Left("Failed to decode Jwt")
  }

  "Decode not valid signature" should "fail validation with error" in {
    val invalidSignatureToken: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
      ".eyJpYXQiOjE1ODg5NzIyNzcsInVzZXJJZCI6MX0.DVemJfHpFpMxFZYUs_vlm0VNPHM_gE-gvXCIEC23b0k"

    val decodeToken: Either[String, JwtUserPayload] = authenticationService.decodeToken(invalidSignatureToken)

    decodeToken shouldBe Left("Failed to decode Jwt")
  }

  "Decode expired token" should "fail validation with error" in {
    val expiredToken: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
      ".eyJleHAiOjE1ODg5NzU4NzcsImlhdCI6MTU4ODk3MjI3NywidXNlcklkIjoxfQ.1FQ2eYftQG1SlDVGQDxWJvci7TSQ3qMqwoRwBj9sU-I"

    val decodeToken: Either[String, JwtUserPayload] = authenticationService.decodeToken(expiredToken)

    decodeToken shouldBe Left("Failed to decode Jwt")
  }

  "Decode correct token" should "correct validation with JwtUserPayload" in {
    val correctToken: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
      ".eyJpYXQiOjE1ODg5NzIyNzcsInVzZXJJZCI6MX0.TqfCIT3Q804XCquEtyCIr8PKQtL0vZ_RjF-mxFnN-4Y"

    val decodeToken: Either[String, JwtUserPayload] = authenticationService.decodeToken(correctToken)

    decodeToken.isRight shouldBe true
  }

  "Get token from header no prefix" should "fail with error" in {

    val headerWithoutPrefix: String = "eyJ0eXAiOiJKV1"

    val token: Either[String, String] = authenticationService.getTokenFromHeader(headerWithoutPrefix)

    token shouldBe Left("Authorization prefix not found")
  }

  "Get token from header incorrect prefix" should "fail with error" in {

    val headerIncorrectPrefix: String = "Berer eyJ0eXAiOiJKV1"

    val token: Either[String, String] = authenticationService.getTokenFromHeader(headerIncorrectPrefix)

    token shouldBe Left("Authorization prefix not found")
  }

  "Get token from header correct prefix" should "Token without spaces" in {

    val headerIncorrectPrefix: String = s"${jwtConfig.prefix} eyJ0eXAiOiJKV1"

    val token: Either[String, String] = authenticationService.getTokenFromHeader(headerIncorrectPrefix)

    token.isRight shouldBe true
    token shouldBe Right("eyJ0eXAiOiJKV1")
  }
}
