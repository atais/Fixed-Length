package com.github.atais.fixedlength

import com.github.atais.util.Write
import shapeless.{::, Generic, HList, HNil}

@annotation.implicitNotFound(msg = "Implicit not found for Encoder[${A}]")
trait Encoder[A] extends Serializable {
  def encode(obj: A): String
}

object Encoder {

  def encode[A](obj: A)(implicit enc: Encoder[A]): String = enc.encode(obj)

  def fixed[A](start: Int, end: Int, align: Alignment = Alignment.Left, padding: Char = ' ', truncate: Truncation = Truncation.None)
              (implicit write: Write[A]): Encoder[A] = {
    new Encoder[A] {
      override def encode(obj: A): String = {
        val value = write.write(obj)
        
        val fieldLength = end - start
        
        val truncatedValue = truncate match {
          case Truncation.Left => value.take(fieldLength)
          case Truncation.Right => value.drop(value.length - fieldLength)
          case Truncation.None => value
        }

        val paddingSpace = fieldLength - truncatedValue.length
        val filler = padding.toString * paddingSpace

        align match {
          case Alignment.Left => truncatedValue + filler
          case Alignment.Right => filler + truncatedValue
        }
      }
    }
  }

  val hnilEncoder = new Encoder[HNil] {
    override def encode(obj: HNil): String = ""
  }

  protected[fixedlength] def merge[A <: HList, B](encoderA: Encoder[A],
                                                  encoderB: Encoder[B],
                                                  obj: B :: A): String = {
    encoderB.encode(obj.head) + encoderA.encode(obj.tail)
  }

  protected[fixedlength] def transform[A, B](encoderA: Encoder[A], obj: B)
                                            (implicit gen: Generic.Aux[B, A]): String = {
    encoderA.encode(gen.to(obj))
  }

  final implicit class HListEncoderEnrichedWithHListSupport[L <: HList](val self: Encoder[L]) extends Serializable {
    def <<:[B](encoderB: Encoder[B]): Encoder[B :: L] = new Encoder[B :: L] {
      override def encode(obj: B :: L): String =
        merge(self, encoderB, obj)
    }

    def as[B](implicit gen: Generic.Aux[B, L]): Encoder[B] = new Encoder[B] {
      override def encode(obj: B): String = transform(self, obj)
    }
  }

  final implicit class EncoderEnrichedWithHListSupport[A](val self: Encoder[A]) extends AnyVal {
    def <<:[B](codecB: Encoder[B]): Encoder[B :: A :: HNil] =
      codecB <<: self <<: hnilEncoder
  }

}

