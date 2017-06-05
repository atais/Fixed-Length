package fixedlenght

import org.scalatest.{FlatSpec, Matchers}

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
    FLParser.encode(exampleC) shouldEqual exampleS
  }

}

// name @FixedLenght(0, 10)
// number @FixedLenght(10, 3)
// manager @FixedLenght(13, 5)
case class Employee(name: String,
                    number: Int,
                    manager: Boolean) {

  def desc: List[FixedLenght[_]] = {
    List(
      FixedLenght(name, (s: String) => s, (s: String) => s, 0, 10),
      FixedLenght(number, (s: Int) => s.toString, _.toInt, 10, 3, Alignment.Right),
      FixedLenght(manager, (s: Boolean) => s.toString, _.toBoolean, 13, 5)
    )
  }

}

object Employee {

  implicit def encoder: FLEncoder[Employee] = new FLEncoder[Employee] {
    def encode(obj: Employee): String = {
      obj.desc.map(f => {
        f.write
      }).foldLeft("")(_ + _)
    }
  }

}