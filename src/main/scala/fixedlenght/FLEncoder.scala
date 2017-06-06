package fixedlenght

import shapeless.{::, HList, HNil, ProductTypeClass, ProductTypeClassCompanion}

trait FLEncoder[A] {
  def encode(obj: A): String
}

object FLEncoder extends ProductTypeClassCompanion[FLEncoder] {

  def fixed[A](enc: A => String, length: Int,
               align: Alignment = Alignment.Left, padding: Char = ' '): FLEncoder[A] = {

    new FLEncoder[A] {
      override def encode(obj: A): String =
        toFixedLengthString(obj)(enc, length, align, padding)

    }
  }


  private def toFixedLengthString[A](obj: A)(enc: (A) => String, length: Int,
                                             align: Alignment, padding: Char) = {
    val value = enc(obj)

    val paddingSpace = length - value.length
    val filler = padding.toString * paddingSpace

    align match {
      case Alignment.Left => value + filler
      case Alignment.Right => filler + value
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
