package fixedlenght

import io.circe

/**
  * Created by msiatkowski on 05.06.17.
  */
object Parser {

  def encode[A](input: A)(implicit enc: Encoder[A]): String = enc.encode(input)

  def decode[A](input: String)(implicit dec: Decoder[A]): Either[circe.Error, A] = dec.decode(input)

}
