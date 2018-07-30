package com.github.atais.read

import cats._
import cats.implicits._
import cats.laws.discipline.MonadErrorTests
import com.github.atais.util.Read
import org.scalacheck.Gen
import org.scalacheck.Arbitrary._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest

class ReadLawTest extends FunSuite with scalatest.Discipline {

  // Input generator to to the Read[Long].read
  // It will be sucessfull ~66% of the time (int and long cases)
  val genReadInput = Gen.frequency(
    (1, arbLong.arbitrary.map(_.toString)),
    (1, arbInt.arbitrary.map(_.toString)),
    (1, arbString.arbitrary))

  val readEqTestSize = 20
  val inputStream = Stream.continually(genReadInput.sample).flatten

  implicit val arbReadInt = Arbitrary(Read.longRead)
  implicit val arbReadUnit = Arbitrary(Read.unitRead)
  implicit val arbReadFunction = Arbitrary(arb[Long => Long].map(Applicative[Read].pure))
  implicit val eqThrowable = new Eq[Throwable] {
    // All exceptions are non-terminating and given exceptions
    // aren't values (being mutable, they implement reference
    // equality), then we can't really test them reliably,
    // especially due to race conditions or outside logic
    // that wraps them (e.g. ExecutionException)
    def eqv(x: Throwable, y: Throwable): Boolean = (x ne null) == (y ne null)
  }
  implicit def eqRead[A: Eq]: Eq[Read[A]] =
    Eq.instance((a, b) => inputStream.take(readEqTestSize).forall(x => a.read(x) === b.read(x)))

  checkAll("Read[Long]", MonadErrorTests[Read, Throwable].monadError[Long, Long, Long])
}
