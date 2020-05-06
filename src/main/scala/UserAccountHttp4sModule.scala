import authentication.AuthenticationService
import cats.effect.{Async, ContextShift}
import com.typesafe.scalalogging.StrictLogging
import config.{JdbcConfig, JwtConfig}
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import org.http4s.HttpRoutes
import org.http4s.server.{AuthMiddleware, Router}
import repository.UserAccountDoobie
import com.softwaremill.macwire.wire
import error.{HttpErrorHandler, UserAccountError, UserAccountHttpErrorHandler}
import middleware.AuthMiddlewareJwt
import pdi.jwt.JwtClaim
import route.UserAccountRoutes
import service.UserAccountServiceImpl

class UserAccountHttp4sModule[F[_]: Async: ContextShift](cfg: JdbcConfig, apiPrefix: String, jwtConfig: JwtConfig) extends StrictLogging {
  val xa: Aux[F, Unit] = Transactor.fromDriverManager[F](cfg.driver, cfg.url, cfg.user, cfg.password)
  val userAccountRep : UserAccountDoobie[F] = wire[UserAccountDoobie[F]]
  val userAccountService: UserAccountServiceImpl[F] = wire[UserAccountServiceImpl[F]]
  val authService: AuthenticationService = wire[AuthenticationService]
  val authMiddleware: AuthMiddlewareJwt[F] = new AuthMiddlewareJwt[F](authService, jwtConfig)

  implicit val errorHandler: HttpErrorHandler[F, UserAccountError] = new UserAccountHttpErrorHandler[F]()

  val middleware: AuthMiddleware[F, JwtClaim] = authMiddleware.authMiddleware
  val routes: HttpRoutes[F] = Router(apiPrefix -> errorHandler.handle(middleware(wire[UserAccountRoutes[F]].routes)))
}
