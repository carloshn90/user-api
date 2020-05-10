package com.carher.payload

import com.carher.UnitSpec
import com.carher.model.UserAccountModel

class JwtUserPayloadTest extends UnitSpec {

  "Jwt user payload" should "have the user account model id" in {
    val id = Some(1L)
    val name = "name-test"
    val surname = "surname-test"
    val username = "username-test"
    val email = "email-test"
    val password = "pass-test"
    val userModel: UserAccountModel = UserAccountModel(id, name, surname, username, email, password)

    val jwtUserPayloadOption: Option[JwtUserPayload] = JwtUserPayload.fromUserAccountModel(userModel)

    jwtUserPayloadOption.isDefined shouldBe true
    jwtUserPayloadOption.map(jwtUser => Some(jwtUser.userId) shouldBe id)
  }

}
