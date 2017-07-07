package com.github.atais.fixedlength.simple

import com.github.atais.fixedlength.{Alignment, Encoder, Parser}
import org.scalatest.{FlatSpec, Matchers}

class EncoderTest extends FlatSpec with Matchers {

  // this will not compile due to lacking Decoder
  // todo: better compilation error (current: diverging implicit expansion for type com.github.atais.fixedlenght.Decoder[com.github.atais.fixedlenght.simple.Employee])
  //  "An example class" should "be serialized" in {
  //    import Employee._
  //    Parser.decode[Employee](exampleString).right.get shouldEqual exampleObject
  //  }

  it should "get deserialized" in {
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
        fixed[Boolean](13, 18)
    }.as[Employee]
  }

}




