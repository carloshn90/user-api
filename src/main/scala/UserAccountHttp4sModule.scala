import authentication.AuthenticationService
import cats.effect.{Async, ContextShift}
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
import config.{JdbcConfig, JwtConfig}
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import org.http4s.HttpRoutes
import org.http4s.server.Router
import repository.UserAccountDoobie
import com.softwaremill.macwire.wire
import error.{HttpErrorHandler, UserAccountError, UserAccountHttpErrorHandler}
import middleware.AuthMiddlewareJwt
import route.{UserAccountAuthedRoutes, UserAccountRoutes}
import service.UserAccountServiceImpl

class UserAccountHttp4sModule[F[_]: Async: ContextShift](cfg: JdbcConfig, apiPrefix: String, jwtConfig: JwtConfig) extends StrictLogging {
  val xa: Aux[F, Unit] = Transactor.fromDriverManager[F](cfg.driver, cfg.url, cfg.user, cfg.password)
  val userAccountRep : UserAccountDoobie[F] = wire[UserAccountDoobie[F]]
  val authService: AuthenticationService = new AuthenticationService(jwtConfig)
  val userAccountService: UserAccountServiceImpl[F] = wire[UserAccountServiceImpl[F]]
  val authMiddleware: AuthMiddlewareJwt[F] = wire[AuthMiddlewareJwt[F]]

  implicit val errorHandler: HttpErrorHandler[F, UserAccountError] = new UserAccountHttpErrorHandler[F]()

  val routes: HttpRoutes[F] = Router(apiPrefix -> errorHandler.handle(wire[UserAccountRoutes[F]].routes <+> wire[UserAccountAuthedRoutes[F]].authRoutes))
}
