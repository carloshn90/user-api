package json
import model.{UserAccount, UserAccountResult}
import cats.Applicative
import cats.effect.{IO, Sync}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import payload.LoginRequestPayload

trait CirceJsonCodecs {

  implicit val userAccountEncoder: Encoder.AsObject[UserAccount] = deriveEncoder[UserAccount]
  implicit def userAccountEntityEncoder[F[_]: Applicative]: EntityEncoder[F, UserAccount] = jsonEncoderOf
  implicit val userAccountDecoder: Decoder[UserAccount] = deriveDecoder[UserAccount]
  implicit def userAccountEntityDecoder[F[_]: Sync]: EntityDecoder[F, UserAccount] = jsonOf

  implicit val userAccountIoEncoder: EntityEncoder[IO, UserAccount] = jsonEncoderOf[IO, UserAccount]
  implicit val userAccountIoDecoder: EntityDecoder[IO, UserAccount] = jsonOf[IO, UserAccount]

  implicit val userAccountResultEncoder: Encoder.AsObject[UserAccountResult] = deriveEncoder[UserAccountResult]
  implicit def userAccountResultEntityEncoder[F[_]: Applicative]: EntityEncoder[F, UserAccountResult] = jsonEncoderOf
  implicit val userAccountResultIoEncoder: EntityEncoder[IO, UserAccountResult] = jsonEncoderOf[IO, UserAccountResult]

  implicit val loginReqPayloadDecoder: Decoder[LoginRequestPayload] = deriveDecoder[LoginRequestPayload]
  implicit def loginReqPayloadEntityDecoder[F[_]: Sync]: EntityDecoder[F, LoginRequestPayload] = jsonOf
}
