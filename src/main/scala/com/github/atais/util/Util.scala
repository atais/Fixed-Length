package com.github.atais.util

import scala.util.{Success, Try}

object Util {

  // https://stackoverflow.com/a/22533154/1549135
  implicit class RichTry[T](t: Try[T]) {
    def toEither: Either[Throwable, T] = t.transform(s => Success(Right(s)), f => Success(Left(f))).get
  }

  implicit class RichEither[A, B](t: Either[A, B]) {
    def toOption: Option[B] = t match {
      case Right(v) => Some(v)
      case Left(_) => None
    }
  }

}
