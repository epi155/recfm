package io.github.epi155.recfm.java;

import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.lang.StemField;
import io.github.epi155.recfm.lang.ValidateField;
import io.github.epi155.recfm.type.*;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

@EqualsAndHashCode(callSuper = true)
class ValidateFieldJava extends ValidateField {
    private final ActionField<FieldAbc> delegateAbc;
    private final ActionField<FieldNum> delegateNum;
    private final ActionField<FieldCustom> delegateCus;
    private final StemField<FieldFiller> delegateFil;
    private final StemField<FieldConstant> delegateVal;
    private final ActionField<FieldDomain> delegateDom;

    public ValidateFieldJava(PrintWriter pw, String name, Defaults defaults) {
        super(pw, name, defaults);
        this.delegateAbc = new JavaFieldAbc(pw, defaults);
        this.delegateNum = new JavaFieldNum(pw);
        this.delegateCus = new JavaFieldCustom(pw);
        this.delegateDom = new JavaFieldDomain(pw);
        this.delegateFil = new JavaFieldFiller(pw, defaults);
        this.delegateVal = new JavaFieldConstant(pw);
    }

    protected void validateGrp(@NotNull FieldGroup fld, int w, int bias, AtomicBoolean firstField) {
        if (fld.isRedefines()) return;
        fld.getFields().forEach(it -> validate(it, w, bias, firstField));
    }

    protected void validateOcc(FieldOccurs fld, int w, int bias, AtomicBoolean firstField) {
        if (fld.isRedefines()) return;
        for (int k = 0, shift = 0; k < fld.getTimes(); k++, shift += fld.getLength()) {
            int backShift = shift;
            fld.getFields().forEach(it -> validate(it, w, bias - backShift, firstField));
        }
    }

    protected void validateFil(FieldFiller fld, int w, int bias, boolean isFirst) {
        delegateFil.validate(fld, w, bias, isFirst);
    }

    protected void validateVal(FieldConstant fld, int w, int bias, boolean isFirst) {
        delegateVal.validate(fld, w, bias, isFirst);
    }

    protected void validateNum(FieldNum fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        delegateNum.validate(fld, w, bias, isFirst);
    }

    protected void validateAbc(FieldAbc fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        delegateAbc.validate(fld, w, bias, isFirst);
    }

    @Override
    protected void validateDom(FieldDomain fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        delegateDom.validate(fld, w, bias, isFirst);
    }

    @Override
    protected void validateCus(FieldCustom fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        delegateCus.validate(fld, w, bias, isFirst);
    }
}
