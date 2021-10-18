package com.github.atais.fixedlength.simple

import com.github.atais.fixedlength.{Alignment, Codec, Parser, Truncation}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CodecTest extends AnyFlatSpec with Matchers with EitherValues {

  behavior of "Codec"

  it should "decode example object properly" in {
    import Employee._
    Parser.decode[Employee](exampleString).value shouldEqual truncatedExampleObject
  }

  it should "encode example object properly" in {
    import Employee._
    Parser.encode(exampleObject) shouldEqual exampleString
  }

  object Employee {

    import Codec._
    import cats.implicits._
    import com.github.atais.util.Read._
    import com.github.atais.util.Write._

    implicit val employeeCodec: Codec[Employee] = {
      fixed[String](0, 10) <<:
        fixed[Option[Int]](10, 13, Alignment.Right) <<:
        fixed[Boolean](13, 18) <<:
        fixed[String](18,24, truncate = Truncation.Right) <<:
        fixed[String](24, 31, truncate = Truncation.Left)
    }.as[Employee]
  }

}




