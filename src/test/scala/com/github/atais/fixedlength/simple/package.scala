package com.github.atais.fixedlength

package object simple {

  val exampleObject = Employee("Stefan", Some(10), true, "xxxstreet", "Utrechtxxx")
  
  val truncatedExampleObject = Employee("Stefan", Some(10), true, "street", "Utrecht")

  val exampleString = "Stefan     10true streetUtrecht"

  case class Employee(name: String, number: Option[Int], manager: Boolean, street: String, city: String)

}
