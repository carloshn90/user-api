package json
import cats.Applicative
import cats.effect.{IO, Sync}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import payload.{JwtUserPayload, LoginRequestPayload, UserAccountPayload, UserAccountResultPayload}

trait CirceJsonCodecs {

  implicit val userAccountPayloadEncoder: Encoder.AsObject[UserAccountPayload] = deriveEncoder[UserAccountPayload]
  implicit def userAccountPayloadEntityEncoder[F[_]: Applicative]: EntityEncoder[F, UserAccountPayload] = jsonEncoderOf
  implicit val userAccountPayloadDecoder: Decoder[UserAccountPayload] = deriveDecoder[UserAccountPayload]
  implicit def userAccountPayloadEntityDecoder[F[_]: Sync]: EntityDecoder[F, UserAccountPayload] = jsonOf

  implicit val userAccountResultEncoder: Encoder.AsObject[UserAccountResultPayload] = deriveEncoder[UserAccountResultPayload]
  implicit def userAccountResultEntityEncoder[F[_]: Applicative]: EntityEncoder[F, UserAccountResultPayload] = jsonEncoderOf
  implicit val userAccountResultIoEncoder: EntityEncoder[IO, UserAccountResultPayload] = jsonEncoderOf[IO, UserAccountResultPayload]

  implicit val loginReqPayloadDecoder: Decoder[LoginRequestPayload] = deriveDecoder[LoginRequestPayload]
  implicit def loginReqPayloadEntityDecoder[F[_]: Sync]: EntityDecoder[F, LoginRequestPayload] = jsonOf

  implicit val jwtUserPayloadDecoder: Decoder[JwtUserPayload] = deriveDecoder[JwtUserPayload]
  implicit val jwtUserPayloadEncoder: Encoder.AsObject[JwtUserPayload] = deriveEncoder[JwtUserPayload]

  implicit def validationListEntityEncoder[F[_]: Applicative]: EntityEncoder[F, List[String]] = jsonEncoderOf
}
