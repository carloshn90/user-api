package com.carher.route

import cats.effect.IO
import com.carher.UnitSpec
import com.carher.http4s.matcher.Http4sResponseMatchers
import com.carher.http4s.util.Http4sUtil.createBody
import com.carher.json.CirceJsonCodecs
import com.carher.payload.LoginRequestPayload
import com.carher.service.UserAccountService
import org.http4s.headers.`WWW-Authenticate`
import org.http4s.{Challenge, Header, Headers, Method, Request, Response, Status}
import org.http4s.implicits._

class UserAccountRoutesTest extends UnitSpec with CirceJsonCodecs with Http4sResponseMatchers[IO] {


  "Get login user success" should "status ok and token" in {
    val token: String = "token-test"
    val loginPayload: LoginRequestPayload = LoginRequestPayload("email-test", "password-test")
    val userAccountServiceMock: UserAccountService[IO] = mock[UserAccountService[IO]]
    val userAccountRoutes: UserAccountRoutes[IO] = new UserAccountRoutes[IO](userAccountServiceMock)

    (userAccountServiceMock.login _).expects(*).returning(IO.pure(Option(token))).once()

    val response: IO[Response[IO]] = userAccountRoutes.routes
      .orNotFound
      .run(Request(
        method = Method.POST,
        uri = uri"/login",
        body = createBody(loginPayload)
      ))

    response should beResponse(Status.Ok, token)
  }

  "Get login user fail" should "status Unauthorized" in {
    val loginPayload: LoginRequestPayload = LoginRequestPayload("email-test", "password-test")
    val userAccountServiceMock: UserAccountService[IO] = mock[UserAccountService[IO]]
    val userAccountRoutes: UserAccountRoutes[IO] = new UserAccountRoutes[IO](userAccountServiceMock)

    (userAccountServiceMock.login _).expects(*).returning(IO.pure(None)).once()

    val response: IO[Response[IO]] = userAccountRoutes.routes
      .orNotFound
      .run(Request(
        method = Method.POST,
        uri = uri"/login",
        body = createBody(loginPayload)
      ))

    val header: Header = `WWW-Authenticate`(Challenge("Basic", "Access to the staging site", Map("charset" -> "UTF-8")))
    response should beResponse(Status.Unauthorized, headers = Headers.of(header))
  }

}
