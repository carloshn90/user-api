package com.carher.http4s.matcher

import cats.MonadError
import cats.effect.IO
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.util.CaseInsensitiveString
import org.http4s.{EntityDecoder, Header, Headers, Response, Status}
import org.scalatest.matchers.{MatchResult, Matcher}
import org.http4s.circe.jsonOf

trait Http4sResponseMatchers[F[_]] {

  implicit def run[A]: IO[A] => A = (io: IO[A]) => io.unsafeRunSync
  implicit def emptyBodyIO: F[EmptyBody] => EmptyBody = (_: F[EmptyBody]) => EmptyBody()
  implicit val emptyBodyDecoder: Decoder[EmptyBody] = deriveDecoder[EmptyBody]
  implicit def emptyBodyEntityDecoder: EntityDecoder[IO, EmptyBody] = jsonOf[IO, EmptyBody]

  def beResponse[A](status: Status = Status.Ok, body: A = EmptyBody(), headers: Headers = Headers.empty)
                   (implicit run: F[Response[F]] => Response[F],
                    run1: F[A] => A,
                    ev: EntityDecoder[F, A],
                    m: MonadError[F, Throwable]): ResponseMatcher[A] =
    ResponseMatcher(ResponseT(status, body, headers))

  case class ResponseMatcher[A](rightResponse: ResponseT[A])
                               (implicit run: F[Response[F]] => Response[F],
                                run1: F[A] => A,
                                ed: EntityDecoder[F, A],
                                me: MonadError[F, Throwable]) extends Matcher[F[Response[F]]] {

    override def apply(leftResponse: F[Response[F]]): MatchResult = {
      decomposeBodyResponse(leftResponse)(run, run1, ed, me) match {
        case (lStatus: Status, lBody: A, lHeaders: Headers) => matchResponse(ResponseT(lStatus, lBody, lHeaders), rightResponse)
        case _                                                      => MatchResult(matches = false, "Error trying to compare responses", "")
      }
    }

    private def decomposeBodyResponse[A](response: F[Response[F]])(implicit run: F[Response[F]] => Response[F],
                                                                   run1: F[A] => A,
                                                                   ed: EntityDecoder[F, A],
                                                                   me: MonadError[F, Throwable]): (Status, A, Headers) = {
      val responseRun = run(response)
      val body = run1(responseRun.as[A])
      val headers: Headers = responseRun.headers
      val status: Status = responseRun.status

      (status, body, headers)
    }

    private def matchResponse[A](leftResponse: ResponseT[A], rightResponse: ResponseT[A]): MatchResult = {
      val statusMatch: Boolean = leftResponse.status == rightResponse.status
      val bodyMatch: Boolean = leftResponse.body == rightResponse.body
      val matchResult: Boolean = statusMatch && bodyMatch && headersMatch(leftResponse.headers, rightResponse.headers)

      MatchResult(matches = matchResult, getErrorMsg(leftResponse, rightResponse), "")
    }

    def headersMatch(leftHeaders: Headers, rightHeaders: Headers): Boolean = {
      val rightHeadersName: List[CaseInsensitiveString] = rightHeaders.toList.map(_.name)
      val leftHeaderFilteredByName: Headers = leftHeaders
        .filter((header: Header) => rightHeadersName.contains(header.name))
      leftHeaderFilteredByName == rightHeaders
    }

    private def getErrorMsg[A](leftResponse: ResponseT[A], rightResponse: ResponseT[A]): String =
      s"(Status, body) no match, expected: (status: ${rightResponse.status}, body: ${rightResponse.body}) -" +
        s" actual: (status: ${leftResponse.status}, body: ${leftResponse.body})"
  }
}

final case class ResponseT[A](status: Status = Status.Ok, body: A, headers: Headers = Headers.empty)

sealed trait Body
final case class EmptyBody(msg: String = "empty-body") extends Body

