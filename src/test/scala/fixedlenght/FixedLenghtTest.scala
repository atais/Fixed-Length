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
    // uncomment those to get it working :-)
    implicit val a = fixed((s: String) => s, 10)
    implicit val b = fixed((s: Int) => s.toString, 3, Alignment.Right)
    implicit val c = fixed((s: Boolean) => s.toString, 5)

    FLParser.encode(exampleC) shouldEqual exampleS
  }

}

case class Employee(name: String, number: Int, manager: Boolean)

object Employee {

  // why it does not see this implicit?
  implicit val employeeEncoder =
    fixed((s: String) => s, 10) ::
      fixed((s: Int) => s.toString, 3, Alignment.Right) ::
      fixed((s: Boolean) => s.toString, 5) ::
      HNil


}