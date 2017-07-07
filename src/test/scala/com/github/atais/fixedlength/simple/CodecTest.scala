package com.github.atais.fixedlength.simple

import com.github.atais.fixedlength.{Alignment, Codec, Parser}
import org.scalatest.{FlatSpec, Matchers}

class CodecTest extends FlatSpec with Matchers {

  "An example class" should "be serialized" in {
    import Employee._
    Parser.decode[Employee](exampleString).right.get shouldEqual exampleObject
  }

  it should "get deserialized" in {
    import Employee._
    Parser.encode(exampleObject) shouldEqual exampleString
  }

  object Employee {

    import com.github.atais.util.Read._
    import cats.implicits._
    import com.github.atais.util.Write._
    import Codec._

    implicit val employeeCodec: Codec[Employee] = {
      fixed[String](0, 10) <<:
        fixed[Option[Int]](10, 13, Alignment.Right) <<:
        fixed[Boolean](13, 18)
    }.as[Employee]
  }

}




