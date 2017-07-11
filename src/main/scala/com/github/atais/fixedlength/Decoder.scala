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

  final implicit class HListDecoderEnrichedWithHListSupport[L <: HList](val self: Decoder[L]) extends Serializable {
    def <<:[B](bDecoder: Decoder[B]): Decoder[B :: L] = new Decoder[B :: L] {
      override def decode(str: String): Either[Throwable, ::[B, L]] = {
        for {
          a <- bDecoder.decode(str).right
          b <- self.decode(str).right
        } yield a :: b
      }
    }

    def as[B](implicit gen: Generic.Aux[B, L]): Decoder[B] = new Decoder[B] {
      override def decode(str: String): Either[Throwable, B] = {
        for {
          d <- self.decode(str).right
        } yield gen.from(d)
      }
    }
  }

  final implicit class DecoderEnrichedWithHListSupport[A](val self: Decoder[A]) extends Serializable {
    def <<:[B](codecB: Decoder[B]): Decoder[B :: A :: HNil] = {

      val lastDecoder = new Decoder[A] {
        override def decode(str: String): Either[Throwable, A] = {
          self.extraCharsAfterEnd(str) match {
            case None => self.decode(str)
            case Some(extra) => Left(new LineLongerThanExpectedException(str, extra))
          }
        }
      }

      codecB <<: lastDecoder <<: hnilDecoder
    }
  }

}
