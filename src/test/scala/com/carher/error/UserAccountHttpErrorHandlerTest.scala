package com.carher.error

import cats.effect._
import com.carher.UnitSpec
import org.http4s.{HttpRoutes, Request}
import org.http4s.implicits._
import org.http4s.Status.InternalServerError
import com.softwaremill.macwire.wire

class UserAccountHttpErrorHandlerTest extends UnitSpec {

  val userAccountHttpErrorHandler: UserAccountHttpErrorHandler[IO] = wire[UserAccountHttpErrorHandler[IO]]

  "Http error handle exception with message" should "response with internal server error and message" in {
    val tesMessage = "test message"
    val errRoute: HttpRoutes[IO] = HttpRoutes.of { case _ => throw new IllegalArgumentException(tesMessage)}

    val handleErrorRoute: HttpRoutes[IO] = userAccountHttpErrorHandler.handle(errRoute)
    val response = handleErrorRoute.orNotFound(Request[IO]())
      .unsafeRunSync()
    val bodyMessageVector = response.bodyAsText
      .compile
      .toVector
      .unsafeRunSync()

    response.status shouldBe InternalServerError
    bodyMessageVector shouldBe Vector(s"Internal server error: $tesMessage")
  }

  "Http error handle exception with message" should "response with internal server error" in {
    val errRoute: HttpRoutes[IO] = HttpRoutes.of { case _ => throw new IllegalArgumentException()}

    val handleErrorRoute: HttpRoutes[IO] = userAccountHttpErrorHandler.handle(errRoute)
    val response = handleErrorRoute.orNotFound(Request[IO]().withEmptyBody)
      .unsafeRunSync()
    val bodyMessageVector = response.bodyAsText
      .compile
      .toVector
      .unsafeRunSync()

    response.status shouldBe InternalServerError
    bodyMessageVector should have size 0
  }

}
