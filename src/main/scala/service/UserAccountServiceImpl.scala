package service

import model.UserAccount
import payload.LoginRequestPayload
import repository.UserAccountRepository

class UserAccountServiceImpl[F[_]](userAccountRep: UserAccountRepository[F]) extends UserAccountService[F]{

  override def login(loginReq: LoginRequestPayload): F[Option[UserAccount]] = userAccountRep.findByEmailAndPassword(loginReq.email, loginReq.password)

  override def select(id: Int): F[Option[UserAccount]] = userAccountRep.select(id)

  override def insert(user: UserAccount): F[Int] = userAccountRep.insert(user)

  override def update(id: Int, user: UserAccount): F[Int] = userAccountRep.update(id, user)

  override def delete(id: Int): F[Int] = userAccountRep.delete(id)
}
