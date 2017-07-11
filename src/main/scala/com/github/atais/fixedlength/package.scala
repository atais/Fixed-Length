package com.github.atais

/**
  * Created by michalsiatkowski on 11.07.2017.
  */
package object fixedlength {

  class LineLongerThanExpectedException(inputLine: String, extra: String) extends Throwable {
    override def getMessage: String = s"Input line [$inputLine] is longer than expected, we would skip [$extra]"
  }

}
