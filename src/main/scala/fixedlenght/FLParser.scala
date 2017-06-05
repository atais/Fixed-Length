package fixedlenght


/**
  * Created by msiatkowski on 05.06.17.
  */
object FLParser {

  def encode[A](input: A)(implicit enc: FLEncoder[A]): String = enc.encode(input)

  //  def decode[A: Decoder](input: String): Either[Error, A] = ???

}

trait FLEncoder[A] {
  def encode(obj: A): String
}

trait FLDecoder[A] {
  def decode(str: String): A
}
