package com.github.atais.util

import scala.util.{Success, Try}

/**
  * Created by msiatkowski on 04.07.17.
  */
object Util {

  // https://stackoverflow.com/a/22533154/1549135
  implicit class RichTry[T](t: Try[T]) {
    def toEither: Either[Throwable, T] = t.transform(s => Success(Right(s)), f => Success(Left(f))).get
  }

}
