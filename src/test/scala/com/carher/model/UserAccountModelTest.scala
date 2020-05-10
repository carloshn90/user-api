package com.carher.model

import com.carher.UnitSpec
import com.carher.payload.UserAccountPayload

class UserAccountModelTest extends UnitSpec {

  "User account model" should "created with the user account payload values" in {
    val name = "name-test"
    val surname = "surname-test"
    val username = "username-test"
    val email = "email-test"
    val password = "pass-test"
    val userPayload: UserAccountPayload = UserAccountPayload(name, surname, username, email, password)

    val userModel: UserAccountModel = UserAccountModel.fromUserAccountPayload(userPayload)

    userModel.id.isDefined shouldBe false
    userModel.name shouldBe name
    userModel.surname shouldBe surname
    userModel.username shouldBe username
    userModel.email shouldBe email
    userModel.password shouldBe password
  }

}
