package model

final case class UserAccount(
                            id: Long,
                            name: String,
                            surname: String,
                            nickname: String,
                            email: String,
                            password: String
                            )
