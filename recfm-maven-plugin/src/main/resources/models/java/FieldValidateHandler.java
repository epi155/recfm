public interface FieldValidateHandler {
    void error(String name, int offset, int length, int column, ValidateError code);
}
