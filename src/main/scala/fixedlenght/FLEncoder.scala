package fixedlenght

import shapeless.{::, Generic, HList, HNil}

trait FLEncoder[A] {
  def encode(obj: A): String
}

object FLEncoder {

  def encode[A](obj: A)(implicit enc: FLEncoder[A]) = enc.encode(obj)

  def fixed[A](enc: A => String, length: Int,
               align: Alignment = Alignment.Left, padding: Char = ' '): FLEncoder[A] = {
    new FLEncoder[A] {
      override def encode(obj: A): String =
        toFixedLengthString(obj)(enc, length, align, padding)
    }
  }

  val hnilCodec = new FLEncoder[HNil] {
    override def encode(obj: HNil): String = ""
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

  final implicit class ValueCodecEnrichedWithHListSupport[A](val self: FLEncoder[A]) extends AnyVal {
    def <<:[B](codecB: FLEncoder[B]): FLEncoder[B :: A :: HNil] =
      codecB <<: self <<: FLEncoder.hnilCodec
  }

  final implicit class HListCodecEnrichedWithHListSupport[L <: HList](val self: FLEncoder[L]) {
    def <<:[B](bEncoder: FLEncoder[B]): FLEncoder[B :: L] = new FLEncoder[B :: L] {
      override def encode(obj: B :: L): String = {
        bEncoder.encode(obj.head) + self.encode(obj.tail)
      }
    }
  }

  implicit def genToYuple[L <: HList, A](implicit encoder: FLEncoder[L], gen: Generic.Aux[A, L]): FLEncoder[A] = new FLEncoder[A] {
    override def encode(obj: A): String = encoder.encode(gen.to(obj))
  }


}

