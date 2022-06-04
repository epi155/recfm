package io.github.epi155.recfm.scala

import io.github.epi155.recfm.scala.ValidateError._

trait FieldValidateHandler {
  def error(name: String, offset: Int, length: Int, column: Int, code: ValidateError): Unit
}
