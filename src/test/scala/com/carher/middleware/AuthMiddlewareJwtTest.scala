package com.carher.middleware

import cats.effect._
import com.carher.UnitSpec
import com.carher.authentication.AuthenticationService
import com.carher.config.JwtConfig
import com.carher.http4s.matcher.Http4sResponseMatchers
import com.carher.payload.JwtUserPayload
import org.http4s.{AuthScheme, AuthedRoutes, Credentials, Headers, Request}
import org.http4s.implicits._
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import com.softwaremill.macwire.wire


class AuthMiddlewareJwtTest extends UnitSpec with Http4sResponseMatchers[IO] {

  val jwtConfig: JwtConfig = JwtConfig(password = "test", prefix = AuthScheme.Bearer.toString(), expirationSeconds = 60)
  val authenticationService: AuthenticationService = wire[AuthenticationService]
  val authMiddlewareJwt: AuthMiddlewareJwt[IO] = wire[AuthMiddlewareJwt[IO]]

  val authedRoutes: AuthedRoutes[JwtUserPayload, IO] = AuthedRoutes.of { case _ => Ok()}


  "Request without Authorization" should "response with status forbidden" in {

    val response = authMiddlewareJwt.authMiddleware(authedRoutes)
      .orNotFound(Request[IO]())

    response should beResponse(Forbidden)
  }

  "Request with correct Jwt token" should "response with status Ok" in {
    val correctToken: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
      ".eyJpYXQiOjE1ODg5NzIyNzcsInVzZXJJZCI6MX0.TqfCIT3Q804XCquEtyCIr8PKQtL0vZ_RjF-mxFnN-4Y"

    val authorizationHeader = Headers.of(Authorization(Credentials.Token(AuthScheme.Bearer, correctToken)))
    val request: Request[IO] = Request[IO](headers = authorizationHeader)

    val response = authMiddlewareJwt.authMiddleware(authedRoutes)
      .orNotFound(request)

    response should beResponse(Ok)
  }

}
