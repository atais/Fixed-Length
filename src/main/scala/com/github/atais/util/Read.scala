package com.github.atais.util

import cats.implicits.catsSyntaxEitherObject
import cats.implicits.catsSyntaxEither


trait Read[A] extends Serializable {

  def read(str: String): Either[Throwable, A]

}

object Read {

  def create[A](f: String => Either[Throwable, A]): Read[A] = new Read[A] {
    override def read(str: String): Either[Throwable, A] = f(str)
  }

  // --------------------
  // implicits

  implicit def optionRead[T](implicit r: Read[T]): Read[Option[T]] = create[Option[T]] {
    s => Right(r.read(s).toOption)
  }

  implicit val shortRead: Read[Short] = create[Short] {
    s => Either.catchNonFatal(s.toShort)
  }

  implicit val intRead: Read[Int] = create[Int] {
    s => Either.catchNonFatal(s.toInt)
  }

  implicit val longRead: Read[Long] = create[Long] {
    s => Either.catchNonFatal(s.toLong)
  }

  implicit val floatRead: Read[Float] = create[Float] {
    s => Either.catchNonFatal(s.toFloat)
  }

  implicit val doubleRead: Read[Double] = create[Double] {
    s => Either.catchNonFatal(s.toDouble)
  }

  implicit val booleanRead: Read[Boolean] = create[Boolean] {
    s => Either.catchNonFatal(s.toBoolean)
  }

  implicit val charRead: Read[Char] = create[Char] {
    s => Either.catchNonFatal(s.head)
  }

  implicit val stringRead: Read[String] = create[String] {
    s => Right(s)
  }
}

