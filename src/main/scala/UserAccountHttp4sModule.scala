import cats.effect.{Async, ContextShift}
import com.typesafe.scalalogging.StrictLogging
import config.JdbcConfig
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import org.http4s.HttpRoutes
import org.http4s.server.Router
import repository.UserAccountDoobie
import com.softwaremill.macwire.wire
import error.{HttpErrorHandler, UserAccountError, UserAccountHttpErrorHandler}
import middleware.AuthMiddleware
import route.UserAccountRoutes
import service.UserAccountServiceImpl

class UserAccountHttp4sModule[F[_]: Async: ContextShift](cfg: JdbcConfig, apiPrefix: String) extends StrictLogging {
  val xa: Aux[F, Unit] = Transactor.fromDriverManager[F](cfg.driver, cfg.url, cfg.user, cfg.password)
  val userAccountRep : UserAccountDoobie[F] = wire[UserAccountDoobie[F]]
  val userAccountService: UserAccountServiceImpl[F] = wire[UserAccountServiceImpl[F]]
  val authMiddleware: AuthMiddleware[F] = wire[AuthMiddleware[F]]

  implicit val errorHandler: HttpErrorHandler[F, UserAccountError] = new UserAccountHttpErrorHandler[F]()

//  val routes: HttpRoutes[F] = Router(apiPrefix -> wire[UserAccountRoutes[F]].routes)
  val routes: HttpRoutes[F] = authMiddleware.to(Router(apiPrefix -> wire[UserAccountRoutes[F]].routes))
}