package io.github.epi155.recfm.lang;

import io.github.epi155.recfm.type.*;
import lombok.Data;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public abstract class ValidateField {
    protected final PrintWriter pw;
    protected final String name;
    protected final Defaults defaults;
    protected int count = 0;

    public void validate(NakedField fld, int padWidth, int bias, AtomicBoolean firstStatement) {
        if (fld instanceof FieldAbc) {
            validateAbc((FieldAbc) fld, padWidth, bias, firstStatement.getAndSet(false));
        } else if (fld instanceof FieldNum) {
            validateNum((FieldNum) fld, padWidth, bias, firstStatement.getAndSet(false));
        } else if (fld instanceof FieldUser) {
            validateUser((FieldUser) fld, padWidth, bias, firstStatement.getAndSet(false));
        } else if (fld instanceof FieldConstant) {
            validateVal((FieldConstant) fld, padWidth, bias, firstStatement.getAndSet(false));
        } else if (fld instanceof FieldFiller) {
            validateFil((FieldFiller) fld, padWidth, bias, firstStatement.getAndSet(false));
        } else if (fld instanceof FieldOccurs) {
            validateOcc((FieldOccurs) fld, padWidth, bias, firstStatement);
        } else if (fld instanceof FieldGroup) {
            validateGrp((FieldGroup) fld, padWidth, bias, firstStatement);
        }
    }

    protected abstract void validateUser(FieldUser fld, int padWidth, int bias, boolean andSet);

    protected abstract void validateGrp(FieldGroup fld, int padWidth, int bias, AtomicBoolean isFirst);

    protected abstract void validateOcc(FieldOccurs fld, int padWidth, int bias, AtomicBoolean isFirst);

    protected abstract void validateFil(FieldFiller fld, int padWidth, int bias, boolean isFirst);

    protected abstract void validateVal(FieldConstant fld, int padWidth, int bias, boolean isFirst);

    protected abstract void validateNum(FieldNum fld, int padWidth, int bias, boolean isFirst);

    protected abstract void validateAbc(FieldAbc fld, int padWidth, int bias, boolean isFirst);

}
