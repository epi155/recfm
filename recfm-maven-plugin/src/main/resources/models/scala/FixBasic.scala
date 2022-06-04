trait FixBasic {
  def validate(handler: FieldValidateHandler): Boolean

  def audit(handler: FieldValidateHandler): Boolean
}
