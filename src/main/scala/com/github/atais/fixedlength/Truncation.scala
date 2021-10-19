package com.github.atais.fixedlength

sealed trait Truncation 

object Truncation {
  
    case object Left extends Truncation
  
    case object Right extends Truncation
    
    case object None extends Truncation

}


