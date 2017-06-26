package read

import read.Read.read

/**
  * Created by michalsiatkowski on 26.06.2017.
  */
package object implicits {

  implicit val intRead: Read[Int] = read[Int] {
    s => Right(s.toInt)
  }

  implicit val booleanRead: Read[Boolean] = read[Boolean] {
    s => Right(s.toBoolean)
  }

  implicit val stringRead: Read[String] = read[String] {
    s => Right(s)
  }

}
