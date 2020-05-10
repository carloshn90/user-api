package com.carher

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}

abstract class UnitSpec extends FlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach
