package com.github.atais.fixedlength

object Parser {

  def encode[A](input: A)(implicit enc: Encoder[A]): String = enc.encode(input)

  def decode[A](input: String)(implicit dec: Decoder[A]): Either[Throwable, A] = dec.decode(input)

}
