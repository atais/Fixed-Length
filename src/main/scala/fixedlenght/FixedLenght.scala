package fixedlenght

/**
  * Created by msiatkowski on 05.06.17.
  */
case class FixedLenght[A](source: A,
                          encode: A => String,
                          decode: String => A,
                          start: Int,
                          lenght: Int,
                          align: Alignment = Alignment.Left,
                          padding: Char = ' ') {

  def write: String = {
    val value = encode(source)

    val paddingSpace = lenght - value.length
    val filler = padding.toString * paddingSpace

    align match {
      case Alignment.Left => value + filler
      case Alignment.Right => filler + value
    }
  }

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

sealed trait Alignment

object Alignment {

  case object Left extends Alignment

  case object Right extends Alignment

}
