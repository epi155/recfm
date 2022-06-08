package io.github.epi155.recfm.scala

trait FieldValidateHandler {
  def error(name: String, offset: Int, length: Int, column: Int, code: ValidateError.ValidateError): Unit
}
