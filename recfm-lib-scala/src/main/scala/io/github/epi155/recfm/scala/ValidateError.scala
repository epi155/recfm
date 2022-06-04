package io.github.epi155.recfm.scala

object ValidateError extends Enumeration {
  type ValidateError = Value
  val NotNumber, NotAscii, NotLatin, NotValid, Mismatch = Value
}
