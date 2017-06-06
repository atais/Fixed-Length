package test

import java.lang.{StringBuilder => StrBuilder}
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets

import cats.Applicative
import scodec.Attempt.{Failure, Successful}
import scodec.bits.{BitVector, _}
import scodec.codecs._
import scodec.{Attempt, Codec, DecodeResult, Encoder, Err, SizeBound}

import scala.annotation.tailrec
import scala.util.Try

object Fixed {

  val charUtf8: Codec[Char] = new Codec[Char] {
    private val charset = StandardCharsets.UTF_8

    private val _1b = bin"0"
    private val _2b = bin"110"
    private val _3b = bin"1110"
    private val _4b = bin"11110"

    private val order = Stream((_1b, 8), (_2b, 16), (_3b, 24), (_4b, 32))

    override val sizeBound: SizeBound = SizeBound.bounded(8, 32)

    override def encode(value: Char): Attempt[BitVector] =
      Attempt.successful(BitVector(charset.encode(CharBuffer.wrap(Array(value)))))

    override def decode(bits: BitVector): Attempt[DecodeResult[Char]] = {
      order.dropWhile(b => !bits.startsWith(b._1)).collect {
        case (_, bitCount) =>
          val (char, remainder) = bits.splitAt(bitCount)
          val charBits = Attempt.fromEither(char.acquire(bitCount).left.map(Err.apply))
          charBits.flatMap(utf8.decode).map(r => DecodeResult(r.value.charAt(0), remainder))
      }.headOption.getOrElse(Attempt.failure(Err("Cannot decode given char")))
    }
  }

  def utf8Sized(length: Int): Codec[String] = new Codec[String] {
    override def decode(bits: BitVector): Attempt[DecodeResult[String]] = decodeLoop(bits, new StrBuilder())

    @tailrec
    def decodeLoop(remainder: BitVector, acc: StrBuilder): Attempt[DecodeResult[String]] = {
      if (acc.length() == length)
        Successful(DecodeResult(acc.toString, remainder))
      else charUtf8.decode(remainder) match {
        case Successful(value) => decodeLoop(value.remainder, acc.append(value.value))
        case f: Failure => f //TODO: review error here!
      }
    }

    override def encode(value: String): Attempt[BitVector] = {
      if (value.length != value.length)
        Failure(Err("The value to be encoded is too big"))
      else {
        Encoder.encodeSeq(charUtf8)(value)
      }
    }

    override def sizeBound: SizeBound = SizeBound.bounded(8 * length, 32 * length)
  }

  def utf8zero(zero: String): Codec[Unit] = utf8Sized(zero.length).narrow[Unit](
    s => if (s == zero) Successful(()) else Failure(Err(s"This codec only encodes $zero")),
    _ => zero
  )

  def fixed(size: Int, fillWith: Char): Codec[String] = new Codec[String] {
    require(size >= 0)

    override def decode(bits: BitVector): Attempt[DecodeResult[String]] =
      utf8Sized(size).decode(bits).map(d => DecodeResult(d.value.dropWhile(_ == fillWith), d.remainder)) //TODO: add left padding

    override def encode(value: String): Attempt[BitVector] = {
      if (value.length > size)
        Failure(Err("The encoded string is too big"))
      else
        utf8Sized(size).encode(List.fill(size - value.length)(fillWith).mkString ++ value)
    }

    override val sizeBound: SizeBound = SizeBound.bounded(size * 8, size * 32)
  }

  implicit val attemptApplicative = new Applicative[Attempt] {
    override def pure[A](x: A): Attempt[A] = Successful(x)

    override def ap[A, B](ff: Attempt[(A) => B])(fa: Attempt[A]): Attempt[B] = {
      ff.flatMap(f => fa.map(f))
    }
  }

  def listCodec[A](delimiter: Codec[Unit])(implicit c: Codec[A]) = new Codec[List[A]] {

    override def decode(bits: BitVector): Attempt[DecodeResult[List[A]]] = {
      decodeLoop(DecodeResult(Nil, bits)).map(d => DecodeResult(d.value.reverse, d.remainder))
    }

    @tailrec
    private def decodeLoop(acc: DecodeResult[List[A]]): Attempt[DecodeResult[List[A]]] = {
      Codec[A].decode(acc.remainder) match {
        case Successful(value) => delimiter.decode(value.remainder) match {
          case Successful(delim) => decodeLoop(DecodeResult(value.value :: acc.value, delim.remainder))
          case Failure(cause) => Successful(DecodeResult(value.value :: acc.value, value.remainder))
        }
        case _: Failure if acc.remainder.isEmpty => Successful(acc)
        case f: Failure => f
      }
    }

    override def encode(value: List[A]): Attempt[BitVector] = {
      import cats.instances.list._
      import cats.syntax.traverse._
      val seq: Attempt[List[BitVector]] = value.map(Codec[A].encode).sequenceU
      seq.flatMap(l => l.foldLeft(Attempt.successful(BitVector.empty)) {
        case (acc, bits) => Applicative[Attempt].map2(acc, delimiter.encode(()))((current, delim) => current ++ delim ++ bits)
      })
    }

    override val sizeBound: SizeBound = SizeBound.unknown
  }

}

object Runner extends App {

  import Fixed._
  import shapeless.HNil

  case class Person(name: String, age: Int)

  val lines =
    "____marcin000300\n\r______test000200\n\r_______joe00030a\n\r"

  val c = fixed(6, '_') :: fixed(3, '0') :: HNil
  val strToInt: String => Attempt[Int] = str => Try(str.toInt) match {
    case scala.util.Failure(ex) => Failure(Err(s"$str is not an int"))
    case scala.util.Success(v) => Successful(v)
  }
  val pc: Codec[shapeless.::[String, shapeless.::[Int, HNil]]] = (("name" | fixed(10, '_')) :: ("age" | fixed(6, '0').narrow[Int](strToInt, _.toString)))
  val personCodec: Codec[Person] = pc.as[Person]

  val p = Fixed.listCodec[Person](Fixed.utf8zero("\n\r"))(personCodec)

  println(p.decode(BitVector(lines.getBytes("UTF-8"))))

}