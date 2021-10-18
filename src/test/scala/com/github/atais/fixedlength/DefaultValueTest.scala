package com.github.atais.fixedlength

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DefaultValueTest extends AnyFlatSpec with Matchers with EitherValues {

  val defaultValue = 0

  case class Example(a: Int, b: Int, c: Int)

  object Example {

    import Decoder._
    import com.github.atais.util.Read._

    implicit val example1: Decoder[Example] = {
      fixed[Int](0, 1) <<:
        fixed[Int](1, 2) <<:
        fixed[Int](2, 3, Alignment.Left, ' ', defaultValue)
    }.as[Example]
  }

  behavior of "Decoder"

  it should "use a default value" in {
    import Example._

    val exampleString = "12"
    val decoded = Parser.decode[Example](exampleString)

    decoded.value.c shouldEqual defaultValue
  }


}




