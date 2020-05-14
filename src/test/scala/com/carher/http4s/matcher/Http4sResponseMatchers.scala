package com.carher.http4s.matcher

import cats.MonadError
import cats.effect.IO
import org.http4s.{EntityDecoder, Response, Status}
import org.scalatest.matchers.{MatchResult, Matcher}

trait Http4sResponseMatchers[F[_]] {

  implicit def run[A]: IO[A] => A = (io: IO[A]) => io.unsafeRunSync

  def beStatus(status: Status)(implicit run: F[Response[F]] => Response[F]): StatusMatcher = StatusMatcher(status)

  def beBody[A](body: A)(implicit run: F[Response[F]] => Response[F],
                         run1: F[A] => A,
                         ev: EntityDecoder[F, A],
                         m: MonadError[F, Throwable]): BodyMatcher[A] = BodyMatcher(body)

  def beStatusAndBody[A](status: Status, body: A)(implicit run: F[Response[F]] => Response[F],
                         run1: F[A] => A,
                         ev: EntityDecoder[F, A],
                         m: MonadError[F, Throwable]): StatusAndBodyMatcher[A] = StatusAndBodyMatcher(status, body)

  case class StatusMatcher(right: Status)(implicit run: F[Response[F]] => Response[F]) extends Matcher[F[Response[F]]] {

    override def apply(left: F[Response[F]]): MatchResult = run(left).status match {
      case rStatus: Status  => MatchResult(rStatus == right, s"Status no match, expected: $right - actual: $rStatus", "")
      case _                => MatchResult(matches = false, "There is not status to compare to", "")
    }
  }

  case class BodyMatcher[A](right: A)(implicit run: F[Response[F]] => Response[F],
                                      run1: F[A] => A,
                                      ed: EntityDecoder[F, A],
                                      me: MonadError[F, Throwable]) extends Matcher[F[Response[F]]] {

    override def apply(left: F[Response[F]]): MatchResult = run1(run(left).as[A]) match {
        case rBody: A => MatchResult(rBody == right, s"Body no match, expected: $right - actual: $rBody", "")
        case _        => MatchResult(matches = false, "There is not body to compare to", "")
      }
  }

  case class StatusAndBodyMatcher[A](rightStatus: Status, rightBody: A)(implicit run: F[Response[F]] => Response[F],
                                      run1: F[A] => A,
                                      ed: EntityDecoder[F, A],
                                      me: MonadError[F, Throwable]) extends Matcher[F[Response[F]]] {

    override def apply(left: F[Response[F]]): MatchResult = {
      val responseRun = run(left)
      (responseRun.status, run1(responseRun.as[A])) match {
        case (rStatus: Status, rBody: A)  => MatchResult(rStatus == rightStatus && rBody == rightBody, s"(Status, body) no match, expected: (status: $rightStatus, body: $rightBody) - actual: (status: $rStatus, body: $rBody)", "")
        case (null, _)                    => MatchResult(matches = false, s"There is not status to compare to", "")
        case (_, null)                    => MatchResult(matches = false, s"There is not body to compare to", "")
        case _                            => MatchResult(matches = false, "There is not body to compare to", "")
      }
    }
  }

}
