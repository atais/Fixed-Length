package com.github.atais.fixedlength

import org.scalatest.{FlatSpec, Matchers}

class ParsingErrorsTest extends FlatSpec with Matchers {

  case class Example(a: Int, b: Int, c: Int)

  object Example {

    import Decoder._
    import com.github.atais.util.Read._

    implicit val example1: Decoder[Example] = {
      fixed[Int](0, 1) <<:
        fixed[Int](1, 2) <<:
        fixed[Int](2, 3)
    }.as[Example]
  }

  behavior of "Decoder"

  it should "return an StringIndexOutOfBoundsException error if input is too short" in {
    import Example._

    val exampleString = "12"
    val decoded = Parser.decode[Example](exampleString)

    decoded.left.get shouldBe a[StringIndexOutOfBoundsException]
    decoded.right.toOption shouldEqual None
  }

  it should "return an ParsingFailedException error if input does not match expected types" in {
    import Example._

    val exampleString = "12-"
    val decoded = Parser.decode[Example](exampleString)

    decoded.left.get shouldBe a[ParsingFailedException]
    decoded.left.get.getMessage should include("-")
    decoded.right.toOption shouldEqual None
  }

  it should "return an LineLongerThanExpectedException error if input line is longer than expected" in {
    import Example._

    val exampleString = "1234"
    val decoded = Parser.decode[Example](exampleString)

    decoded.left.get shouldBe a[LineLongerThanExpectedException]
    decoded.left.get.getMessage should include(exampleString)
    decoded.right.toOption shouldEqual None
  }


}




