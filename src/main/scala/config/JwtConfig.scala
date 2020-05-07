package config

final case class JwtConfig(password: String, prefix: String, expirationSeconds: Long)
