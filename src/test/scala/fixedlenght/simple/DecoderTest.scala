package fixedlenght.simple

import fixedlenght.{Alignment, Decoder, Parser}
import org.scalatest.{FlatSpec, Matchers}

class DecoderTest extends FlatSpec with Matchers {

  "An example class" should "be serialized" in {
    import Employee._
    Parser.decode[Employee](exampleString).right.get shouldEqual exampleObject
  }

  // this will not compile due to lacking Encoder
  // todo: better compilation error (current: diverging implicit expansion for type fixedlenght.Encoder[fixedlenght.simple.Employee])
  //  it should "not deserialized" in {
  //    import Employee._
  //    Parser.encode(exampleObject) shouldEqual exampleString
  //  }

  object Employee {

    import shapeless._
    import read.implicits._
    import Decoder._

    implicit val employeeCodec: Decoder[::[String, ::[Int, ::[Boolean, HNil]]]] = {
      fixed[String](0, 10) <<:
        fixed[Int](10, 13, Alignment.Right) <<:
        fixed[Boolean](13, 18)
    }
  }

}



