package com.carher

import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

abstract class UnitSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach  with MockFactory
