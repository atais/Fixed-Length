package fixedlenght

import cats.Show
import read.Read
import shapeless.{::, Generic, HList, HNil}

/**
  * Created by michalsiatkowski on 26.06.2017.
  */
trait Codec[A] extends Encoder[A] with Decoder[A]

object Codec {

  def fixed[A](start: Int, end: Int, align: Alignment = Alignment.Left, padding: Char = ' ')
              (implicit reader: Read[A], show: Show[A]): Codec[A] = {

    new Codec[A] {
      override def decode(str: String): Either[Throwable, A] =
        Decoder.decode(str)(Decoder.fixed[A](start, end, align, padding)(reader))

      override def encode(obj: A): String =
        Encoder.encode(obj)(Encoder.fixed[A](start, end, align, padding)(show))
    }
  }

  val hnilCodec = new Codec[HNil] {
    override def decode(str: String): Either[Throwable, HNil] = Decoder.hnilDecoder.decode(str)

    override def encode(obj: HNil): String = Encoder.hnilEncoder.encode(obj)
  }

  final implicit class HListCodecEnrichedWithHListSupport[L <: HList](val self: Codec[L]) {
    def <<:[B](bCodec: Codec[B]): Codec[B :: L] = new Codec[B :: L] {

      override def decode(str: String): Either[Throwable, ::[B, L]] = {
        for {
          a <- bCodec.decode(str).right
          b <- self.decode(str).right
        } yield a :: b
      }

      override def encode(obj: ::[B, L]): String =
        bCodec.encode(obj.head) + self.encode(obj.tail)
    }
  }

  final implicit class CodecEnrichedWithHListSupport[A](val self: Codec[A]) extends AnyVal {
    def <<:[B](codecB: Codec[B]): Codec[B :: A :: HNil] =
      codecB <<: self <<: hnilCodec
  }

  implicit def HListToA[L <: HList, A](implicit codec: Codec[L], gen: Generic.Aux[A, L]): Codec[A] = new Codec[A] {
    override def encode(obj: A): String =
      codec.encode(gen.to(obj))

    override def decode(str: String): Either[Throwable, A] =
      for {
        d <- codec.decode(str).right
      } yield gen.from(d)
  }

}