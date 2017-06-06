package fixedlenght

import shapeless._

/**
  * Created by msiatkowski on 05.06.17.
  */
object FLParser {

  def encode[A](input: A)(implicit enc: FLEncoder[A]): String = enc.encode(input)

  //  def decode[A: Decoder](input: String): Either[Error, A] = ???

}

trait FLEncoder[A] {
  def encode(obj: A): String
}

object FLEncoder {

  def fixed[A](enc: A => String, lenght: Int,
               align: Alignment = Alignment.Left, padding: Char = ' '): FLEncoder[A] = {

    new FLEncoder[A] {
      override def encode(obj: A): String = {
        val value = enc(obj)

        val paddingSpace = lenght - value.length
        val filler = padding.toString * paddingSpace

        align match {
          case Alignment.Left => value + filler
          case Alignment.Right => filler + value
        }
      }
    }
  }

  implicit object FLEncoderTC extends ProductTypeClass[FLEncoder] {
    override def product[H, T <: HList](ch: FLEncoder[H], ct: FLEncoder[T]): FLEncoder[shapeless.::[H, T]] = {
      new FLEncoder[H :: T] {
        override def encode(obj: ::[H, T]): String = ch.encode(obj.head) + ct.encode(obj.tail)
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

  object FLEncoderTCHelper extends ProductTypeClassCompanion[FLEncoder] {
    override val typeClass: ProductTypeClass[FLEncoder] = FLEncoderTC
  }

}


trait FLDecoder[A] {
  def decode(str: String): A
}
