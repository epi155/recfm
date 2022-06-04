package io.github.epi155.recfm.java;

public interface FixBasic {
    boolean validate(FieldValidateHandler handler);

    boolean audit(FieldValidateHandler handler);

    String encode();
}
