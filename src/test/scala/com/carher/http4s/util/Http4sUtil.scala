package com.carher.http4s.util
import fs2.Stream
import io.circe.Encoder

object Http4sUtil {

  def createBody[A](obj: A)(implicit encoder: Encoder[A]): Stream[fs2.Pure, Byte] = {
    val json: String = encoder(obj).noSpaces
    Stream.emits(json.toArray.map(json => json.toByte))
  }
}
