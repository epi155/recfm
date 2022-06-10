object FixError {
  @SerialVersionUID(1L)
  class FieldOverFlowException(s: String) extends RuntimeException(s) {
  }

  @SerialVersionUID(1L)
  class FieldUnderFlowException(s: String) extends RuntimeException(s) {
  }

  @SerialVersionUID(1L)
  class InvalidNumberException(s: String) extends RuntimeException(s) {
  }

  @SerialVersionUID(1L)
  class RecordOverflowException(s: String) extends RuntimeException(s) {
  }

  @SerialVersionUID(1L)
  class RecordUnderflowException(s: String) extends RuntimeException(s) {
  }

  class NotAsciiException(c: Char, u: Int) extends FixError.SetterException(String.format("Offending char: U+%04X @+%d", c.toInt, u + 1), 3) {
  }

  class NotLatinException(c: Int, u: Int) extends FixError.SetterException(String.format("Offending char: U+%04X @+%d", c, u + 1), 3) {
  }

  class NotValidException(c: Char, u: Int) extends FixError.SetterException(String.format("Offending char: U+%04X @+%d", c.toInt, u + 1), 3) {
  }

  protected class SetterException(s: String, deep: Int) extends RuntimeException(s) {
    fillInStackTrace
    setStackTrace(getStackTrace.drop(deep))
  }

}
