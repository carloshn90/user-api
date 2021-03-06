package com.carher.json

import cats.Applicative
import cats.effect.Sync
import com.carher.payload.{JwtUserPayload, LoginRequestPayload, UserAccountPayload, UserAccountResultPayload}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

trait CirceJsonCodecs {

  implicit val userAccountPayloadEncoder: Encoder.AsObject[UserAccountPayload] = deriveEncoder[UserAccountPayload]
  implicit def userAccountPayloadEntityEncoder[F[_]: Applicative]: EntityEncoder[F, UserAccountPayload] = jsonEncoderOf
  implicit val userAccountPayloadDecoder: Decoder[UserAccountPayload] = deriveDecoder[UserAccountPayload]
  implicit def userAccountPayloadEntityDecoder[F[_]: Sync]: EntityDecoder[F, UserAccountPayload] = jsonOf

  implicit val userAccountResultEncoder: Encoder.AsObject[UserAccountResultPayload] = deriveEncoder[UserAccountResultPayload]
  implicit def userAccountResultEntityEncoder[F[_]: Applicative]: EntityEncoder[F, UserAccountResultPayload] = jsonEncoderOf
  implicit val userAccountResultDecoder: Decoder[UserAccountResultPayload] = deriveDecoder[UserAccountResultPayload]
  implicit def userAccountResultEntityDecoder[F[_]: Sync]: EntityDecoder[F, UserAccountResultPayload] = jsonOf

  implicit val loginReqPayloadEncoder: Encoder.AsObject[LoginRequestPayload] = deriveEncoder[LoginRequestPayload]
  implicit def loginReqPayloadEntityEncoder[F[_]: Applicative]: EntityEncoder[F, LoginRequestPayload] = jsonEncoderOf
  implicit val loginReqPayloadDecoder: Decoder[LoginRequestPayload] = deriveDecoder[LoginRequestPayload]
  implicit def loginReqPayloadEntityDecoder[F[_]: Sync]: EntityDecoder[F, LoginRequestPayload] = jsonOf

  implicit val jwtUserPayloadDecoder: Decoder[JwtUserPayload] = deriveDecoder[JwtUserPayload]
  implicit val jwtUserPayloadEncoder: Encoder.AsObject[JwtUserPayload] = deriveEncoder[JwtUserPayload]

  implicit def validationListEntityEncoder[F[_]: Applicative]: EntityEncoder[F, List[String]] = jsonEncoderOf
}
