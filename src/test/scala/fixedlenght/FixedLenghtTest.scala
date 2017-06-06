package fixedlenght

import org.scalatest.{FlatSpec, Matchers}
import shapeless.HNil

/**
  * Created by SAnders on 03.11.2016.
  */
class FixedLenghtTest extends FlatSpec with Matchers {

  val exampleC = Employee("Stefan", 10, true)
  val exampleS = "Stefan     10true "

  "An example class" should "be serialized" in {
    //    Encoder.encode(exampleS)
  }

  it should "get deserialized" in {
    import FLEncoder.fixed

    implicit val a = fixed((s: String) => s, 10)
    implicit val b = fixed((s: Int) => s.toString, 3, Alignment.Right)
    implicit val c = fixed((s: Boolean) => s.toString, 5)

    FLParser.encode(exampleC) shouldEqual exampleS
  }

}

// name @FixedLenght(0, 10)
// number @FixedLenght(10, 3)
// manager @FixedLenght(13, 5)
case class Employee(name: String,
                    number: Int,
                    manager: Boolean)

object Employee {

  //    FixedLenght( ::
  //    FixedLenght(, _.toInt, 10, 3, Alignment.Right) ::
  //    FixedLenght(, _.toBoolean, 13, 5) ::
  import FLEncoder.fixed

  implicit val employeeEncoder =
    fixed((s: String) => s, 10) ::
      fixed((s: Int) => s.toString, 3, Alignment.Right) ::
      fixed((s: Boolean) => s.toString, 5) :: HNil


}