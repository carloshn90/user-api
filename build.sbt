name := "user-api"

version := "0.1"

scalaVersion := "2.13.2"

fork in Test := true

envVars in Test := Map(
  "APP_CONFIG_PATH" -> "src/test/resources/application-test.conf",
  "LC_CTYPE"        -> "en_US.UTF-8",
  "LC_ALL"          -> "en_US.UTF-8"
)

val logbackVersion = "1.1.3"
val doobieVersion = "0.8.8"
val http4sVersion = "0.21.4"
val circeVersion = "0.13.0"
val wireVersion = "2.3.3"
val pureConfigVersion = "0.12.3"
val jwtVersion = "4.2.0"
val otjPgVersion = "0.13.3"
val scalaMockVersion = "4.4.0"
val scalaTestVersion = "3.1.0"

libraryDependencies ++= Seq(

//  Logback
  "ch.qos.logback" % "logback-classic" % logbackVersion % Runtime,

//  Doobie
  "org.tpolecat" %% "doobie-core"      % doobieVersion,
  "org.tpolecat" %% "doobie-hikari"    % doobieVersion,          // HikariCP transactor.
  "org.tpolecat" %% "doobie-postgres"  % doobieVersion,          // Postgres driver 42.2.9 + type mappings.
  "org.tpolecat" %% "doobie-quill"     % doobieVersion,          // Support for Quill 3.4.10
  "org.tpolecat" %% "doobie-h2"        % doobieVersion % "test", // H2 driver 1.4.200 + type mappings.
  "org.tpolecat" %% "doobie-specs2"    % doobieVersion % "test", // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test", // ScalaTest support for typechecking statements.

//  http4s
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,

//  Circe
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

//  Wire
  "com.softwaremill.macwire" %% "macros" % wireVersion,

//  Pure Config
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,

//  JWT
  "com.pauldijou" %% "jwt-core" % jwtVersion,
  "com.pauldijou" %% "jwt-circe" % jwtVersion,

//  Embedded postgesql
  "com.opentable.components" % "otj-pg-embedded" % otjPgVersion % Test,

  "org.scalamock" %% "scalamock" % scalaMockVersion % Test,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
)
