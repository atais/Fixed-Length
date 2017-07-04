package com.github.atais.read

import com.github.atais.read.Read.read
import com.github.atais.util.Util._

import scala.util.Try

/**
  * Created by michalsiatkowski on 26.06.2017.
  */
package object implicits {

  implicit val shortRead: Read[Short] = read[Short] {
    s => Try(s.toShort).toEither
  }

  implicit val intRead: Read[Int] = read[Int] {
    s => Try(s.toInt).toEither
  }

  implicit val longRead: Read[Long] = read[Long] {
    s => Try(s.toLong).toEither
  }

  implicit val floatRead: Read[Float] = read[Float] {
    s => Try(s.toFloat).toEither
  }

  implicit val doubleRead: Read[Double] = read[Double] {
    s => Try(s.toDouble).toEither
  }

  implicit val booleanRead: Read[Boolean] = read[Boolean] {
    s => Try(s.toBoolean).toEither
  }

  implicit val charRead: Read[Char] = read[Char] {
    s => Try(s.head).toEither
  }

  implicit val stringRead: Read[String] = read[String] {
    s => Try(s).toEither
  }

}
