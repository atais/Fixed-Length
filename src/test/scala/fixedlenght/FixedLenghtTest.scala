package fixedlenght

import fixedlenght.FLEncoder._
import org.scalatest.{FlatSpec, Matchers}
import shapeless.HNil

class FixedLenghtTest extends FlatSpec with Matchers {



  val exampleC = Employee("Stefan", 10, true)
  val exampleS = "Stefan     10true "

  "An example class" should "be serialized" in {
    //    Encoder.encode(exampleS)
  }

  it should "get deserialized" in {
    import Employee._
    FLParser.encode(exampleC) shouldEqual exampleS
  }

}

case class Employee(name: String, number: Int, manager: Boolean)

object Employee {

  implicit val employeeEncoder: FLEncoder[shapeless.::[String, shapeless.::[Int, shapeless.::[Boolean, HNil]]]] =
    fixed((s: String) => s, 10) <<:
      fixed((s: Int) => s.toString, 3, Alignment.Right) <<:
      fixed((s: Boolean) => s.toString, 5)
}

