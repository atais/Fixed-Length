package com.github.atais.fixedlenght

/**
  * Created by michalsiatkowski on 26.06.2017.
  */
package object simple {

  val exampleObject = Employee("Stefan", 10, true)

  val exampleString = "Stefan     10true "

  case class Employee(name: String, number: Int, manager: Boolean)

}
