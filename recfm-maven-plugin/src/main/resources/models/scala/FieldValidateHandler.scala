trait FieldValidateHandler {
  def error(name: String, offset: Int, length: Int, column: Int, code: ValidateError): Unit
}
