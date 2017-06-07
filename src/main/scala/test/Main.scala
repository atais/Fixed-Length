package test

/**
  * Created by msiatkowski on 06.06.17.
  */

import shapeless._

trait FLEncoder[A] { self =>
  def encode(obj: A): String

  def comap[B](f: B => A): FLEncoder[B] = FLEncoder.fixed[B](obj => encode(f(obj)))
}

object FLEncoder /*extends ProductTypeClassCompanion[FLEncoder] */{

  def encode[A](obj: A)(implicit enc: FLEncoder[A]) = enc.encode(obj)

  def fixed[A](enc: A => String): FLEncoder[A] = {
    new FLEncoder[A] {
      override def encode(obj: A): String =
        enc(obj)
    }
  }

    implicit class EncoderAsGeneric[L <: HList, I <: HList](l: L)(implicit L: ListOfEncoder.Aux[L, I]) {
      def as[E](implicit gen: Generic.Aux[E, I]) = L.merge(l).comap(gen.to)
    }

}

case class Employee(name: String, surname: String, number: Int, manager: Boolean)

object Employee {

  import FLEncoder.fixed

  implicit val employeeEncoder: FLEncoder[Employee] =
    (fixed((s: String) => s) ::
    fixed((s: String) => s) ::
    fixed((s: Int) => s.toString) ::
    fixed((s: Boolean) => s.toString) ::
      HNil).as[Employee]
}


trait ListOfEncoder[L <: HList] {
  type Inside <: HList
  def merge(l: L): FLEncoder[Inside]
}

object ListOfEncoder {
  type Aux[L <: HList, I <: HList] = ListOfEncoder[L] { type Inside = I }

  implicit val hnil: Aux[HNil, HNil] = new ListOfEncoder[HNil] {
    type Inside = HNil
    def merge(l: HNil) = FLEncoder.fixed(_ => "")
  }

  implicit def hcons[H, T <: HList](implicit T: ListOfEncoder[T]): Aux[FLEncoder[H] :: T, H :: T.Inside] = new ListOfEncoder[FLEncoder[H] :: T] {
    type Inside = H :: T.Inside
    def merge(l: FLEncoder[H] :: T): FLEncoder[H :: T.Inside] =
      FLEncoder.fixed((ht: H :: T.Inside) => l.head.encode(ht.head) + T.merge(l.tail).encode(ht.tail))
  }
}

object Main extends App {
  val example = Employee("Stefan", "aaa", 10, true)

  println(FLEncoder.encode(example))

}
