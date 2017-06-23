package read

/**
  * Created by msiatkowski on 23.06.17.
  */
trait Read[A] {
  def read(str: String): Either[Throwable, A]
}

object Read {

  def read[A](f: String => Either[Throwable, A]): Read[A] = new Read[A] {
    override def read(str: String): Either[Throwable, A] = f(str)
  }

  implicit val intRead: Read[Int] = read[Int]{
    s => Right(s.toInt)
  }

  implicit val booleanRead: Read[Boolean] = read[Boolean]{
    s => Right(s.toBoolean)
  }

  implicit val stringRead: Read[String] = read[String]{
    s => Right(s)
  }

}

