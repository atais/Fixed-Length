package com.github.atais.fixedlength

import org.scalatest.{FlatSpec, Matchers}

class TooLongLineTest extends FlatSpec with Matchers {

  case class Example(a: Char, b: Option[Char], c: Char)

  object Example {

    import Decoder._
    import com.github.atais.util.Read._

    implicit val example1: Decoder[Example] = {
      fixed[Char](0, 1) <<:
        fixed[Option[Char]](1, 2) <<:
        fixed[Char](2, 3)
    }.as[Example]
  }

  behavior of "Decoder"

  it should "return an error if input line is longer than expected" in {
    import Example._

    val exampleString = "abcd"
    val decoded = Parser.decode[Example](exampleString)

    decoded.left.get shouldBe a[LineLongerThanExpectedException]
    decoded.right.toOption shouldEqual None
  }


}




