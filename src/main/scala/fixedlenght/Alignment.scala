package fixedlenght


sealed trait Alignment

object Alignment {

  case object Left extends Alignment

  case object Right extends Alignment

}
