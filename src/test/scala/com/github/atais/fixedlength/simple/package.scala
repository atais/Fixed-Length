package com.github.atais.fixedlength

package object simple {

  val exampleObject = Employee("Stefan", Some(10), true, "xxxstreet", "Utrechtxxx", "xx1000AB", "NLxx")
  
  val truncatedExampleObject = Employee("Stefan", Some(10), true, "street", "Utrecht", "1000AB", "NL")

  val exampleString = "Stefan     10true streetUtrecht1000ABNL"

  case class Employee(name: String, number: Option[Int], manager: Boolean, street: String, city: String, postalCode: String, country: String)

}
