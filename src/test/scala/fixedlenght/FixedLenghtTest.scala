package fixedlenght

import org.scalatest.{FlatSpec, Matchers}

class FixedLenghtTest extends FlatSpec with Matchers {

  val exampleC = Employee("Stefan", 10, true)
  val exampleS = "Stefan     10true "

  "An example class" should "be serialized" in {
    import Employee._
    Parser.decode[Employee](exampleS).right.get shouldEqual exampleC
  }

  it should "get deserialized" in {
    import Employee._
    Parser.encode(exampleC) shouldEqual exampleS
  }

}

case class Employee(name: String, number: Int, manager: Boolean)

object Employee {

  import cats.implicits._
  import shapeless._
  import read.Read._

  implicit val employeeEncoder: Encoder[::[String, ::[Int, ::[Boolean, HNil]]]] = {
    import Encoder.fixed
    fixed[String](0, 10) <<:
      fixed[Int](10, 13, Alignment.Right) <<:
      fixed[Boolean](13, 18)
  }

  implicit val employeeDecoder: Decoder[::[String, ::[Int, ::[Boolean, HNil]]]] = {
    import Decoder.fixed
    fixed[String](0, 10) <<:
      fixed[Int](10, 13, Alignment.Right) <<:
      fixed[Boolean](13, 18)
  }
}

