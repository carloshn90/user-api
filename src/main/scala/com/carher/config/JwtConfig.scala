package com.carher.config

final case class JwtConfig(password: String, prefix: String, expirationSeconds: Long)
