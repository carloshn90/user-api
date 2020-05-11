package com.carher.service

import com.carher.payload.{LoginRequestPayload, UserAccountPayload}

trait UserAccountService[F[_]] {

  def login(loginReq: LoginRequestPayload): F[Option[String]]
  def findById(id: Long): F[Option[UserAccountPayload]]
  def insert(user: UserAccountPayload): F[Either[List[String], Long]]
  def update(id: Long, user: UserAccountPayload): F[Either[List[String], Long]]
  def delete(id: Long): F[Int]
}
