package fixedlenght

import cats._
import shapeless.{::, Generic, HList, HNil}

trait Encoder[A] {
  def encode(obj: A): String
}

object Encoder {

  def encode[A](obj: A)(implicit enc: Encoder[A]): String = enc.encode(obj)

  def fixed[A](start: Int, end: Int, align: Alignment = Alignment.Left, padding: Char = ' ')
              (implicit show: Show[A]): Encoder[A] = {
    new Encoder[A] {
      override def encode(obj: A): String = {
        val value = show.show(obj)

        val paddingSpace = (end - start) - value.length
        val filler = padding.toString * paddingSpace

        align match {
          case Alignment.Left => value + filler
          case Alignment.Right => filler + value
        }
      }
    }
  }

  val hnilEncoder = new Encoder[HNil] {
    override def encode(obj: HNil): String = ""
  }

  final implicit class HListEncoderEnrichedWithHListSupport[L <: HList](val self: Encoder[L]) {
    def <<:[B](bEncoder: Encoder[B]): Encoder[B :: L] = new Encoder[B :: L] {
      override def encode(obj: B :: L): String =
      bEncoder.encode(obj.head) + self.encode(obj.tail)
    }
  }

  final implicit class EncoderEnrichedWithHListSupport[A](val self: Encoder[A]) extends AnyVal {
    def <<:[B](codecB: Encoder[B]): Encoder[B :: A :: HNil] =
      codecB <<: self <<: hnilEncoder
  }

  implicit def HListToA[L <: HList, A](implicit encoder: Encoder[L], gen: Generic.Aux[A, L]): Encoder[A] = new Encoder[A] {
    override def encode(obj: A): String =
      encoder.encode(gen.to(obj))
  }

}

