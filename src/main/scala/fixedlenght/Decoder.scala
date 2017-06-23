package fixedlenght

import read.Read
import shapeless.{::, Generic, HList, HNil}


/**
  * Created by msiatkowski on 06.06.17.
  */
trait Decoder[A] {
  def decode(str: String): Either[Throwable, A]
}

object Decoder {

  def decode[A](str: String)(implicit dec: Decoder[A]): Either[Throwable, A] = dec.decode(str)

  def fixed[A](start: Int, end: Int, align: Alignment = Alignment.Left, padding: Char = ' ')
              (implicit reader: Read[A]): Decoder[A] = {
    new Decoder[A] {
      override def decode(str: String): Either[Throwable, A] = {
        val part = str.substring(start, end)

        val stripped = align match {
          case Alignment.Left => stripTrailing(part, padding)
          case Alignment.Right => stripLeading(part, padding)
        }

        reader.read(stripped)
      }

      private def stripLeading(s: String, c: Char): String = s.replaceFirst(s"""^$c*""", "")

      private def stripTrailing(s: String, c: Char): String = s.replaceFirst(s"""$c*$$""", "")
    }
  }

  val hnilDecoder = new Decoder[HNil] {
    override def decode(str: String): Either[Throwable, HNil] = Right(HNil)
  }

  final implicit class DecoderEnrichedWithHListSupport[A](val self: Decoder[A]) extends AnyVal {
    def <<:[B](codecB: Decoder[B]): Decoder[B :: A :: HNil] =
      codecB <<: self <<: Decoder.hnilDecoder
  }

  final implicit class HListDecoderEnrichedWithHListSupport[L <: HList](val self: Decoder[L]) {
    def <<:[B](bDecoder: Decoder[B]): Decoder[B :: L] = new Decoder[B :: L] {
      override def decode(str: String): Either[Throwable, ::[B, L]] = {
        val bd = bDecoder.decode(str)
        val sd = self.decode(str)

        for {
          a <- bd.right
          b <- sd.right
        } yield a :: b
      }
    }
  }

  implicit def HListToA[L <: HList, A](implicit decoder: Decoder[L], gen: Generic.Aux[A, L]): Decoder[A] = new Decoder[A] {
    override def decode(str: String): Either[Throwable, A] = {
      for {
        d <- decoder.decode(str).right
      } yield gen.from(d)
    }
  }

}