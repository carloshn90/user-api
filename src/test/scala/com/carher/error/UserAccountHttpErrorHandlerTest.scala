package com.carher.error

import cats.effect._
import com.carher.UnitSpec
import com.carher.http4s.matcher.Http4sResponseMatchers
import org.http4s.{HttpRoutes, Request}
import org.http4s.implicits._
import org.http4s.Status.InternalServerError
import com.softwaremill.macwire.wire

class UserAccountHttpErrorHandlerTest extends UnitSpec with Http4sResponseMatchers[IO] {

  val userAccountHttpErrorHandler: UserAccountHttpErrorHandler[IO] = wire[UserAccountHttpErrorHandler[IO]]

  "Http error handle exception with message" should "response with internal server error and message" in {
    val tesMessage = "test message"
    val errRoute: HttpRoutes[IO] = HttpRoutes.of { case _ => throw new IllegalArgumentException(tesMessage)}

    val handleErrorRoute: HttpRoutes[IO] = userAccountHttpErrorHandler.handle(errRoute)
    val response = handleErrorRoute.orNotFound(Request[IO]())

    response should beResponse(InternalServerError, s"Internal server error: $tesMessage")
  }

  "Http error handle exception with message" should "response with internal server error" in {
    val errRoute: HttpRoutes[IO] = HttpRoutes.of { case _ => throw new IllegalArgumentException()}

    val handleErrorRoute: HttpRoutes[IO] = userAccountHttpErrorHandler.handle(errRoute)
    val response = handleErrorRoute.orNotFound(Request[IO]().withEmptyBody)

    response should beResponse(InternalServerError, "")
  }

}
