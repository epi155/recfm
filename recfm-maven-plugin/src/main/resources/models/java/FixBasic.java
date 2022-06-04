public interface FixBasic {
    boolean validate(FieldValidateHandler handler);
    boolean audit(FieldValidateHandler handler);
    String encode();
}
