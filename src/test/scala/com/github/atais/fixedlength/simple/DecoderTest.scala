package com.github.atais.fixedlength.simple

import com.github.atais.fixedlength.{Alignment, Decoder, Parser}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DecoderTest extends AnyFlatSpec with Matchers with EitherValues {

  behavior of "Decoder"

  it should "decode example object properly" in {
    import Employee._
    Parser.decode[Employee](exampleString).value shouldEqual truncatedExampleObject
  }

  // this will not compile due to lacking Encoder
  // todo: better compilation error (current: diverging implicit expansion for type com.github.atais.fixedlenght.Encoder[com.github.atais.fixedlenght.simple.Employee])
  //  it should "not deserialized" in {
  //    import Employee._
  //    Parser.encode(exampleObject) shouldEqual exampleString
  //  }

  object Employee {

    import Decoder._
    import com.github.atais.util.Read._

    implicit val employeeCodec: Decoder[Employee] = {
      fixed[String](0, 10) <<:
        fixed[Option[Int]](10, 13, Alignment.Right) <<:
        fixed[Boolean](13, 18) <<:
        fixed[String](18, 24) <<:
        fixed[String](24, 31) <<:
        fixed[String](31, 37, Alignment.Right) <<:
        fixed[String](37, 39, Alignment.Right)
    }.as[Employee]
  }

}



