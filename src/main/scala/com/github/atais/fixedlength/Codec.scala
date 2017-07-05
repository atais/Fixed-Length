package com.github.atais.fixedlength

import cats.Show
import com.github.atais.read.Read
import shapeless.{::, HList, HNil}

/**
  * Created by michalsiatkowski on 26.06.2017.
  */
trait Codec[A] extends Encoder[A] with Decoder[A] with Serializable

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

  final implicit class HListCodecEnrichedWithHListSupport[L <: HList](val self: Codec[L]) extends Serializable {
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

}