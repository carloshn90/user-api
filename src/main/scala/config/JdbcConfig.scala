package config

final case class JdbcConfig(
                             host: String,
                             port: Int,
                             dbName: String,
                             url: String,
                             driver: String,
                             user: String,
                             password: String,
                             connectionTimeout: Long,
                             maximumPoolSize: Int
                         )
