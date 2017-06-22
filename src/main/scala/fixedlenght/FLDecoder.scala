package fixedlenght

/**
  * Created by msiatkowski on 06.06.17.
  */
trait FLDecoder[A] {
  def decode(str: String): Either[Throwable, (A, String)]
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