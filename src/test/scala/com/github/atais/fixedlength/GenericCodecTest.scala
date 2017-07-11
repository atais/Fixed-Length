package com.github.atais.fixedlength

import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class GenericCodecTest extends FlatSpec with Matchers with PropertyChecks {

  implicit val generatorDrivenConf = PropertyCheckConfiguration(minSuccessful = 5000)

  case class Example(a: Char, b: Option[Char], c: Char)

  object Example {

    import Codec._
    import cats.implicits._
    import com.github.atais.util.Read._
    import com.github.atais.util.Write._

    implicit val example1: Codec[Example] = {
      fixed[Char](0, 1) <<:
        fixed[Option[Char]](1, 2) <<:
        fixed[Char](2, 3)
    }.as[Example]
  }

  "Any example class" should "be parsed properly" in {
    forAll { (a: Char, b: Option[Char], c: Char) => {
      if (!(a == ' ' || b == Some(' ') || c == ' ')) {
        val m = Example(a, b, c)
        import Example._

        val stringed = m.a.toString + m.b.getOrElse(" ").toString + m.c.toString
        val encoded = Parser.encode[Example](m)

        encoded shouldEqual stringed

        val decoded = Parser.decode[Example](encoded)
        decoded.right.get shouldEqual m
      }
    }
    }
  }

}
