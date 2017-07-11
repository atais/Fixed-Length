package com.github.atais.fixedlength

import com.github.atais.util.Read
import shapeless.{::, Generic, HList, HNil}

import scala.util.{Failure, Success, Try}


@annotation.implicitNotFound(msg = "Implicit not found for Decoder[${A}]")
trait Decoder[A] extends Serializable {
  def decode(str: String): Either[Throwable, A]

  protected[fixedlength] def extraCharsAfterEnd(source: String): Option[String] = None
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

        reader.read(stripped) match {
          case Right(p) => Right(p)
          case Left(e) => Left(new Throwable(s"Failed parsing [$part], described with [$start, $end, $align, $padding]. Error: ${e.getMessage}"))
        }
      }

      override protected[fixedlength] def extraCharsAfterEnd(source: String): Option[String] = {
        Try(source.substring(end)) match {
          case Success(s) => if (s.length == 0) None else Some(s)
          case Failure(_) => None
        }
      }

      private def stripLeading(s: String, c: Char): String = s.replaceFirst(s"""^$c*""", "")

      private def stripTrailing(s: String, c: Char): String = s.replaceFirst(s"""$c*$$""", "")
    }
  }

  val hnilDecoder = new Decoder[HNil] {
    override def decode(str: String): Either[Throwable, HNil] = Right(HNil)
  }

  protected[fixedlength] def merge[A <: HList, B](decoderA: Decoder[A],
                                                  decoderB: Decoder[B],
                                                  str: String): Either[Throwable, ::[B, A]] = {
    for {
      a <- decoderB.decode(str).right
      b <- decoderA.decode(str).right
    } yield a :: b
  }

  protected[fixedlength] def transform[A, B](decoderA: Decoder[A], str: String)
                                            (implicit gen: Generic.Aux[B, A]): Either[Throwable, B] = {
    for {
      d <- decoderA.decode(str).right
    } yield gen.from(d)
  }

  protected[fixedlength] def decodeLast[A](decoderA: Decoder[A], str: String): Either[Throwable, A] = {
    decoderA.extraCharsAfterEnd(str) match {
      case None => decoderA.decode(str)
      case Some(extra) => Left(new LineLongerThanExpectedException(str, extra))
    }
  }

  final implicit class HListDecoderEnrichedWithHListSupport[L <: HList](val self: Decoder[L]) extends Serializable {
    def <<:[B](decoderB: Decoder[B]): Decoder[B :: L] = new Decoder[::[B, L]] {
      override def decode(str: String): Either[Throwable, ::[B, L]] =
        merge(self, decoderB, str)
    }

    def as[B](implicit gen: Generic.Aux[B, L]): Decoder[B] = new Decoder[B] {
      override def decode(str: String): Either[Throwable, B] =
        transform(self, str)
    }
  }

  final implicit class DecoderEnrichedWithHListSupport[A](val self: Decoder[A]) extends Serializable {
    def <<:[B](codecB: Decoder[B]): Decoder[B :: A :: HNil] = {

      val lastDecoder = new Decoder[A] {
        override def decode(str: String): Either[Throwable, A] =
          decodeLast(self, str)
      }

      codecB <<: lastDecoder <<: hnilDecoder
    }
  }

}
