package fixedlenght.simple

import fixedlenght.{Alignment, Codec, Parser}
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

    import shapeless._
    import cats.implicits._
    import read.implicits._
    import Codec._

    implicit val employeeCodec: Codec[::[String, ::[Int, ::[Boolean, HNil]]]] = {
      fixed[String](0, 10) <<:
        fixed[Int](10, 13, Alignment.Right) <<:
        fixed[Boolean](13, 18)
    }
  }

}




