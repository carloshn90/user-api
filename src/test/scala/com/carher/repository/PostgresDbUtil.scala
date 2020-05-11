package com.carher.repository

import cats.effect.{Blocker, ContextShift, IO}
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext

object PostgresDbUtil {

  private val postgres: EmbeddedPostgres = EmbeddedPostgres.builder().start()

  implicit private val ioContextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def createPostgresTransactor: Transactor[IO] = Transactor
    .fromDriverManager[IO](
      "org.postgresql.Driver",
      postgres.getJdbcUrl("postgres", "postgres"),
      "postgres",
      "postgres",
      Blocker.liftExecutionContext(ExecutionContext.global)
    )
}
