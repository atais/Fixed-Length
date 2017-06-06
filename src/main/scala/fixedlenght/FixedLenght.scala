package fixedlenght

/**
  * Created by msiatkowski on 05.06.17.
  */
case class FixedLenght[A](
                          encode: A => String,
                          decode: String => A,
                          start: Int,
                          lenght: Int,
                          align: Alignment = Alignment.Left,
                          padding: Char = ' ') {



  //  def read(source: String): A = {
  //    val stripped = align match {
  //      case Alignment.Left => stripTrailing(source, padding)
  //      case Alignment.Right => stripLeading(source, padding)
  //    }
  //
  //    decode(stripped)
  //  }
  //
  //  private def stripLeading(s: String, c: Char): String = s.replaceFirst(s"""^$c*""", "")
  //
  //  private def stripTrailing(s: String, c: Char): String = s.replaceFirst(s"""$c*$$""", "")

}

object FixedLenght {
  implicit def encoder[A](inst: A): FLEncoder[FixedLenght[A]] = new FLEncoder[FixedLenght[A]] {
    override def encode(obj: FixedLenght[A]): String =
      write(obj.encode(inst), obj.lenght, obj.align, obj.padding)
  }

   private def write(value: String, lenght: Int, align: Alignment, padding: Char): String = {
      val paddingSpace = lenght - value.length
      val filler = padding.toString * paddingSpace

      align match {
        case Alignment.Left => value + filler
        case Alignment.Right => filler + value
      }
    }
}

sealed trait Alignment

object Alignment {

  case object Left extends Alignment

  case object Right extends Alignment

}
