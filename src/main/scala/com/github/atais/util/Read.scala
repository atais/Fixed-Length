package com.github.atais.util

import cats.implicits.catsSyntaxEitherObject
import cats.implicits.catsSyntaxEither
import cats.MonadError
import scala.annotation.tailrec

trait Read[A] extends Serializable { self =>

  def read(str: String): Either[Throwable, A]

  /**
    * lift function A => B to Read[A] => Read[B]
    */
  def map[B](f: A => B): Read[B] = new Read[B] {
    def read(str: String): Either[Throwable, B] = self.read(str).map(f)
  }

  /**
    * Monadically bind function f over Read
    */
  def flatMap[B](f: A => Read[B]): Read[B] = new Read[B] {
    def read(str: String): Either[Throwable, B] = self.read(str) match {
      case Right(v) => f(v).read(str)
      case Left(err) => Left(err)
    }
  }

  /**
    * Create a new instances that can recover errors (and potentially fail again)
    */
  def handleWithError(f: Throwable => Read[A]): Read[A] = new Read[A] {
    def read(str: String): Either[Throwable, A] = self.read(str) match {
      case Right(v) => Right(v)
      case Left(err) => f(err).read(str)
    }
  }

  /**
    * Create a new instance that recovers from all potential errors
    */
  def handleWithAllError(f: Throwable => A): Read[A] =
    handleWithError(f andThen Read.const)

}

object Read {

  def create[A](f: String => Either[Throwable, A]): Read[A] = new Read[A] {
    override def read(str: String): Either[Throwable, A] = f(str)
  }

  /**
    * Creates a constant Read instance. Useful together with flatMap
    */
  def const[A](a: A): Read[A] = create(_ => Right(a))

  /**
    * Creates a Read instance that always fails
    */
  def failed[A](err: Throwable): Read[A] = new Read[A] {
    def read(str: String): Either[Throwable, A] = Left(err)
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

  implicit val unitRead: Read[Unit] = create[Unit] {
    _ => Right(())
  }

  implicit final val monadReadInstance = new MonadError[Read, Throwable] {
    def raiseError[A](e: Throwable): Read[A] = failed(e)
    def handleErrorWith[A](fa: Read[A])(f: Throwable => Read[A]): Read[A] = fa.handleWithError(f)
    def flatMap[A, B](fa: Read[A])(f: A => Read[B]): Read[B] = fa.flatMap(f)
    def tailRecM[A, B](a: A)(f: A => Read[Either[A, B]]): Read[B] = new Read[B] {
      @tailrec
      def step(str: String, a: A): Either[Throwable, B] = f(a).read(str) match {
        case Left(l) => Left(l)
        case Right(Left(a1)) => step(str, a1)
        case Right(Right(a1)) => Right(a1)
      }

      def read(str: String): Either[Throwable, B] = step(str, a)
    }
    def pure[A](a: A): Read[A] = const(a)
  }
}

