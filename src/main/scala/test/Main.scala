package test

/**
  * Created by msiatkowski on 06.06.17.
  */

import shapeless._

trait FLEncoder[A] {
  def encode(obj: A): String
}

object FLEncoder extends ProductTypeClassCompanion[FLEncoder] {

  def encode[A](obj: A)(implicit enc: FLEncoder[A]) = enc.encode(obj)

  def fixed[A](enc: A => String): FLEncoder[A] = {
    new FLEncoder[A] {
      override def encode(obj: A): String =
        enc(obj)
    }
  }

  // https://stackoverflow.com/questions/24321482/composing-typeclasses-for-tuples-in-scala
  // https://www.scala-exercises.org/shapeless/auto_typeclass_derivation
  override val typeClass: ProductTypeClass[FLEncoder] = new ProductTypeClass[FLEncoder] {

    override def product[H, T <: HList](ch: FLEncoder[H], ct: FLEncoder[T]): FLEncoder[H :: T] = {
      new FLEncoder[H :: T] {
        override def encode(obj: H :: T): String = ch.encode(obj.head) + ct.encode(obj.tail)
      }
    }

    override def emptyProduct: FLEncoder[HNil] = new FLEncoder[HNil] {
      override def encode(obj: HNil): String = ""
    }

    override def project[F, G](instance: => FLEncoder[G], to: (F) => G, from: (G) => F): FLEncoder[F] =
      new FLEncoder[F] {
        override def encode(obj: F): String = instance.encode(to(obj))
      }
  }
}

case class Employee(name: String, number: Int, manager: Boolean)

object Employee {

  import FLEncoder.fixed

  implicit val employeeEncoder =
    fixed((s: String) => s) ::
      fixed((s: Int) => s.toString) ::
      fixed((s: Boolean) => s.toString) ::
      HNil
}

object Main extends App {
  val example = Employee("Stefan", 10, true)

  import FLEncoder._

//  implicit val a = fixed((s: String) => s)
//  implicit val b = fixed((s: Int) => s.toString)
//  implicit val c = fixed((s: Boolean) => s.toString)

  println(encode(example))

}
