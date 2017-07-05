package com.github.atais.util

import scala.util.Try

trait Read[A] extends Serializable {

  def read(str: String): Either[Throwable, A]

}

object Read {

  def create[A](f: String => Either[Throwable, A]): Read[A] = new Read[A] {
    override def read(str: String): Either[Throwable, A] = f(str)
  }

  // --------------------
  // implicits
  import Util._

  implicit def optionRead[T](implicit r: Read[T]): Read[Option[T]] = create[Option[T]] {
    s => Right(r.read(s).toOption)
  }

  implicit val shortRead: Read[Short] = create[Short] {
    s => Try(s.toShort).toEither
  }

  implicit val intRead: Read[Int] = create[Int] {
    s => Try(s.toInt).toEither
  }

  implicit val longRead: Read[Long] = create[Long] {
    s => Try(s.toLong).toEither
  }

  implicit val floatRead: Read[Float] = create[Float] {
    s => Try(s.toFloat).toEither
  }

  implicit val doubleRead: Read[Double] = create[Double] {
    s => Try(s.toDouble).toEither
  }

  implicit val booleanRead: Read[Boolean] = create[Boolean] {
    s => Try(s.toBoolean).toEither
  }

  implicit val charRead: Read[Char] = create[Char] {
    s => Try(s.head).toEither
  }

  implicit val stringRead: Read[String] = create[String] {
    s => Try(s).toEither
  }
}

