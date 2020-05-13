package com.carher.route

import cats.effect.IO
import cats.implicits._
import com.carher.UnitSpec
import com.carher.json.CirceJsonCodecs
import com.carher.matcher.http4s.Http4sResponseMatchers
import com.carher.mock.middleware.AuthMiddlewareMock
import com.carher.payload.{UserAccountPayload, UserAccountResultPayload}
import com.carher.service.UserAccountService
import com.softwaremill.macwire.wire
import fs2.Stream
import org.http4s.{Method, Request, Response, Status}
import org.http4s.implicits._
import io.circe.syntax._

import scala.language.postfixOps

class UserAccountAuthedRoutesTest extends UnitSpec with CirceJsonCodecs with Http4sResponseMatchers[IO] {

  implicit def run[A]: IO[A] => A = (x: IO[A]) => x.unsafeRunSync

  private val userAccountPayload: UserAccountPayload = UserAccountPayload("name-test", "surname-test", "username", "email-test", "password-test")
  private val authMiddlewareMock: AuthMiddlewareMock[IO] = wire[AuthMiddlewareMock[IO]]

  "Get userAccount authorized" should "status ok and the user payload mock" in {
    val userAccountServiceMock: UserAccountService[IO] = mock[UserAccountService[IO]]
    val userAccountAuthedRoutes: UserAccountAuthedRoutes[IO] = new UserAccountAuthedRoutes[IO](
      userAccountServiceMock, authMiddlewareMock.success
    )

    (userAccountServiceMock.findById _).expects(*).returning(IO.pure(Option(userAccountPayload))).once()

    val response: IO[Response[IO]] = userAccountAuthedRoutes.authRoutes
      .orNotFound
      .run(Request(method = Method.GET, uri = uri"/"))

    response should beStatusAndBody(Status.Ok, userAccountPayload)
  }

  "Get userAccount not authorized" should "status forbidden" in {
    val userAccountServiceMock: UserAccountService[IO] = mock[UserAccountService[IO]]
    val userAccountAuthedRoutes: UserAccountAuthedRoutes[IO] = new UserAccountAuthedRoutes[IO](
      userAccountServiceMock, authMiddlewareMock.forbidden
    )

    val response: IO[Response[IO]] = userAccountAuthedRoutes.authRoutes
      .orNotFound
      .run(Request(method = Method.GET, uri = uri"/"))

    response should beStatus(Status.Forbidden)
  }

  "Post create userAccount authorized" should "status created and user id" in {
    val idInserted = 1L
    val insertedOk: Either[List[String], Long] = Either.right[List[String], Long](idInserted)
    val userAccountServiceMock: UserAccountService[IO] = mock[UserAccountService[IO]]
    val userPayloadJson: String = userAccountPayload.asJson.noSpaces
    val userAccountAuthedRoutes: UserAccountAuthedRoutes[IO] = new UserAccountAuthedRoutes[IO](
      userAccountServiceMock, authMiddlewareMock.success
    )

    (userAccountServiceMock.insert _).expects(*).returning(IO.pure(insertedOk)).once()

    val response: IO[Response[IO]] = userAccountAuthedRoutes.authRoutes
      .orNotFound
      .run(Request(
        method = Method.POST,
        uri = uri"/",
        body = Stream.emits(userPayloadJson.toArray.map(json => json.toByte))
      ))

    response should beStatusAndBody(Status.Created, UserAccountResultPayload(idInserted))
  }

  "Post create userAccount some error" should "status bad request" in {
    val insertedError: Either[List[String], Long] = Either.left[List[String], Long](List("error"))
    val userAccountServiceMock: UserAccountService[IO] = mock[UserAccountService[IO]]
    val userPayloadJson: String = userAccountPayload.asJson.noSpaces
    val userAccountAuthedRoutes: UserAccountAuthedRoutes[IO] = new UserAccountAuthedRoutes[IO](
      userAccountServiceMock, authMiddlewareMock.success
    )

    (userAccountServiceMock.insert _).expects(*).returning(IO.pure(insertedError)).once()

    val response: IO[Response[IO]] = userAccountAuthedRoutes.authRoutes
      .orNotFound
      .run(Request(
        method = Method.POST,
        uri = uri"/",
        body = Stream.emits(userPayloadJson.toArray.map(json => json.toByte))
      ))

    response should beStatus(Status.BadRequest)
  }

  "Post create userAccount not authorized" should "status forbidden" in {

    val userAccountServiceMock: UserAccountService[IO] = mock[UserAccountService[IO]]
    val userAccountAuthedRoutes: UserAccountAuthedRoutes[IO] = new UserAccountAuthedRoutes[IO](
      userAccountServiceMock, authMiddlewareMock.forbidden
    )

    val response: IO[Response[IO]] = userAccountAuthedRoutes.authRoutes
      .orNotFound
      .run(Request(method = Method.POST, uri = uri"/"))

    response should beStatus(Status.Forbidden)
  }

  "Put update userAccount authorized" should "status ok and user id" in {
    val idUpdated = 1L
    val updatedOk: Either[List[String], Long] = Either.right[List[String], Long](idUpdated)
    val userAccountServiceMock: UserAccountService[IO] = mock[UserAccountService[IO]]
    val userPayloadJson: String = userAccountPayload.asJson.noSpaces
    val userAccountAuthedRoutes: UserAccountAuthedRoutes[IO] = new UserAccountAuthedRoutes[IO](
      userAccountServiceMock, authMiddlewareMock.success
    )

    (userAccountServiceMock.update _).expects(*, *).returning(IO.pure(updatedOk)).once()

    val response: IO[Response[IO]] = userAccountAuthedRoutes.authRoutes
      .orNotFound
      .run(Request(
        method = Method.PUT,
        uri = uri"/",
        body = Stream.emits(userPayloadJson.toArray.map(json => json.toByte))
      ))

    response should beStatusAndBody(Status.Ok, UserAccountResultPayload(idUpdated))
  }

  "Put update userAccount some error" should "status bad request" in {
    val updatedError: Either[List[String], Long] = Either.left[List[String], Long](List("error"))
    val userAccountServiceMock: UserAccountService[IO] = mock[UserAccountService[IO]]
    val userPayloadJson: String = userAccountPayload.asJson.noSpaces
    val userAccountAuthedRoutes: UserAccountAuthedRoutes[IO] = new UserAccountAuthedRoutes[IO](
      userAccountServiceMock, authMiddlewareMock.success
    )

    (userAccountServiceMock.update _).expects(*, *).returning(IO.pure(updatedError)).once()

    val response: IO[Response[IO]] = userAccountAuthedRoutes.authRoutes
      .orNotFound
      .run(Request(
        method = Method.PUT,
        uri = uri"/",
        body = Stream.emits(userPayloadJson.toArray.map(json => json.toByte))
      ))

    response should beStatus(Status.BadRequest)
  }

  "Put update userAccount not authorized" should "status forbidden" in {

    val userAccountServiceMock: UserAccountService[IO] = mock[UserAccountService[IO]]
    val userAccountAuthedRoutes: UserAccountAuthedRoutes[IO] = new UserAccountAuthedRoutes[IO](
      userAccountServiceMock, authMiddlewareMock.forbidden
    )

    val response: IO[Response[IO]] = userAccountAuthedRoutes.authRoutes
      .orNotFound
      .run(Request(method = Method.PUT, uri = uri"/"))

    response should beStatus(Status.Forbidden)
  }
}
