package com.github.atais.fixedlength.simple

import com.github.atais.fixedlength.{Alignment, Encoder, Parser, Truncation}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EncoderTest extends AnyFlatSpec with Matchers {

  behavior of "Encoder"

  // this will not compile due to lacking Decoder
  // todo: better compilation error (current: diverging implicit expansion for type com.github.atais.fixedlenght.Decoder[com.github.atais.fixedlenght.simple.Employee])
  //  "An example class" should "be serialized" in {
  //    import Employee._
  //    Parser.decode[Employee](exampleString).right.get shouldEqual exampleObject
  //  }

  it should "encode example object properly" in {
    import Employee._
    Parser.encode(exampleObject) shouldEqual exampleString
  }

  object Employee {

    import cats.implicits._
    import com.github.atais.util.Write._
    import Encoder._

    implicit val employeeCodec: Encoder[Employee] = {
      fixed[String](0, 10) <<:
          fixed[Option[Int]](10, 13, Alignment.Right) <<:
          fixed[Boolean](13, 18) <<:
          fixed[String](18, 24, truncate = Truncation.Right) <<:
          fixed[String](24, 31, truncate = Truncation.Left) <<:
          fixed[String](31, 37, Alignment.Right, truncate = Truncation.Right) <<:
          fixed[String](37, 39, Alignment.Right, truncate = Truncation.Left)
    }.as[Employee]
  }

}




