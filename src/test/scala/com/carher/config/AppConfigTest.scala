package com.carher.config

import com.carher.UnitSpec

class AppConfigTest extends UnitSpec {

  "Load configuration" should "correctly loaded" in {
    val (server, jdbc, jwtConf, _) =
      AppConfig.load.fold(e => fail(e.toList.mkString("\n")), identity)

    server.apiUrl shouldBe "/api/test/user-accounts"
    server.host shouldBe "localhost"
    server.port shouldBe 1000

    jdbc.host shouldBe "localhost"
    jdbc.port shouldBe 5432
    jdbc.dbName shouldBe "postgres"
    jdbc.url shouldBe s"jdbc:postgresql://${jdbc.host}:${jdbc.port}/${jdbc.dbName}"
    jdbc.driver shouldBe "org.postgresql.Driver"
    jdbc.user shouldBe "postgres"
    jdbc.password shouldBe "password"
    jdbc.connectionTimeout shouldBe 3000
    jdbc.maximumPoolSize shouldBe 10

    jwtConf.password shouldBe "test"
    jwtConf.prefix shouldBe "Bearer"
    jwtConf.expirationSeconds shouldBe 600
  }

}
