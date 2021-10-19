package com.github.atais.fixedlength

import com.github.atais.util.{Read, Write}
import shapeless.{::, Generic, HList, HNil}

trait Codec[A] extends Encoder[A] with Decoder[A] with Serializable

object Codec {

  def fixed[A: Read : Write](start: Int, end: Int, align: Alignment = Alignment.Left,
                             padding: Char = ' ', defaultValue: A = null.asInstanceOf[A], truncate: Truncation = Truncation.None): Codec[A] = {

    new Codec[A] {
      override def decode(str: String): Either[Throwable, A] =
        Decoder.decode(str)(Decoder.fixed[A](start, end, align, padding, defaultValue))

      override def encode(obj: A): String =
        Encoder.encode(obj)(Encoder.fixed[A](start, end, align, padding, truncate))
    }
  }

  val hnilCodec = new Codec[HNil] {
    override def decode(str: String): Either[Throwable, HNil] = Decoder.hnilDecoder.decode(str)

    override def encode(obj: HNil): String = Encoder.hnilEncoder.encode(obj)
  }

  final implicit class HListCodecEnrichedWithHListSupport[L <: HList](val self: Codec[L]) extends Serializable {
    def <<:[B](bCodec: Codec[B]): Codec[B :: L] = new Codec[B :: L] {

      override def decode(str: String): Either[Throwable, ::[B, L]] =
        Decoder.merge(self, bCodec, str)

      override def encode(obj: ::[B, L]): String =
        Encoder.merge(self, bCodec, obj)
    }

    def as[B](implicit gen: Generic.Aux[B, L]): Codec[B] = new Codec[B] {
      override def decode(str: String): Either[Throwable, B] =
        Decoder.transform(self, str)

      override def encode(obj: B): String =
        Encoder.transform(self, obj)
    }
  }

  final implicit class CodecEnrichedWithHListSupport[A](val self: Codec[A]) extends Serializable {
    def <<:[B](codecB: Codec[B]): Codec[B :: A :: HNil] = {

      val lastCodec = new Codec[A] {
        override def decode(str: String): Either[Throwable, A] =
          Decoder.decodeLast(self, str)

        override def encode(obj: A): String = self.encode(obj)
      }

      codecB <<: lastCodec <<: hnilCodec
    }
  }

}