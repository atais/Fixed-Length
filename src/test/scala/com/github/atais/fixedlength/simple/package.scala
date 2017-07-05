package com.github.atais.fixedlength

package object simple {

  val exampleObject = Employee("Stefan", Some(10), true)

  val exampleString = "Stefan     10true "

  case class Employee(name: String, number: Option[Int], manager: Boolean)

}
