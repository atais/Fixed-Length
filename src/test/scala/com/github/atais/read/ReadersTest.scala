package com.github.atais.read

import com.github.atais.util.Read._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import cats.implicits._

class ReadersTest extends FlatSpec with Matchers with PropertyChecks {

  implicit val generatorDrivenConf = PropertyCheckConfiguration(minSuccessful = 5000)

  "Any short" should "be parsed properly" in {
    forAll { m: Short => test(shortRead.read(m.toString), m) }
  }

  "Any int" should "be parsed properly" in {
    forAll { m: Int => test(intRead.read(m.toString), m) }
  }

  "Any long" should "be parsed properly" in {
    forAll { m: Long => test(longRead.read(m.toString), m) }
  }

  "Any float" should "be parsed properly" in {
    forAll { m: Float => test(floatRead.read(m.toString), m) }
  }

  "Any double" should "be parsed properly" in {
    forAll { m: Double => test(doubleRead.read(m.toString), m) }
  }

  "Any boolean" should "be parsed properly" in {
    forAll { m: Boolean => test(booleanRead.read(m.toString), m) }
  }

  "Any char" should "be parsed properly" in {
    forAll { m: Char => test(charRead.read(m.toString), m) }
  }

  "Any string" should "be parsed properly" in {
    forAll { m: String => test(stringRead.read(m.toString), m) }
  }

  "Unit" should "be parsed properly" in {
    forAll { m: String => test(unitRead.read(m), ()) }
  }

  "Const" should "create an instance with a constant value" in {
    forAll { (m1: String, m2: String) => test(const(m1).read(m2), m1) }
  }

  "Read" should "be transformable" in {
    forAll { m: Int => test(stringRead.read(m.toString).map(_.toInt), m) }
  }

  "Read" should "should behave as an applicative" in {
    forAll { m: Int =>
      case class Product(i: Int, l: Long, s: String)

      val productRead = (intRead, longRead, stringRead).mapN(Product.apply)

      test(productRead.read(m.toString), Product(m, m.toLong, m.toString))
    }
  }

  "Read" should "behave as a monad" in {
    forAll { m: Int =>
      val xor = (key: Int) => intRead.map(_ ^ key)
      val reader = intRead.flatMap(xor).flatMap(xor)

      test(reader.read(m.toString), m)
    }
  }

  "Read" should "be able to recover from errors" in {
    forAll { (a: Int, b: Int) =>
      val error = new IllegalStateException("Error")
      val reader = create(_ => Left(error)).handleWithAllError(_ => b)

      test(reader.read(a.toString), b)
    }
  }

  private def test[A](returned: Either[Throwable, A], checkValue: A) = {
    returned match {
      case Right(v) => v shouldEqual checkValue
      case Left(_) => fail()
    }
  }

}
