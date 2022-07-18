package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.lang.StemField;
import io.github.epi155.recfm.lang.ValidateField;
import io.github.epi155.recfm.type.*;
import lombok.EqualsAndHashCode;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

@EqualsAndHashCode(callSuper = true)
public class ValidateFieldScala extends ValidateField {
    private final ActionField<FieldAbc> delegateAbc;
    private final ActionField<FieldNum> delegateNum;
    private final ActionField<FieldCustom> delegateCus;
    private final StemField<FieldFiller> delegateFil;
    private final StemField<FieldConstant> delegateVal;
    private final StemField<FieldDomain> delegateDom;

    public ValidateFieldScala(PrintWriter pw, String name, Defaults defaults) {
        super(pw, name, defaults);
        this.delegateAbc = new ScalaFieldAbc(pw, defaults);
        this.delegateNum = new ScalaFieldNum(pw);
        this.delegateCus = new ScalaFieldCustom(pw);
        this.delegateDom = new ScalaFieldDomain(pw, name);
        this.delegateFil = new ScalaFieldFiller(pw, defaults);
        this.delegateVal = new ScalaFieldConstant(pw, name);
    }

    @Override
    protected void validateGrp(FieldGroup fld, int w, int bias, AtomicBoolean isFirst) {
        if (fld.isRedefines()) return;
        fld.getFields().forEach(it -> validate(it, w, bias, isFirst));
    }

    @Override
    protected void validateOcc(FieldOccurs fld, int w, int bias, AtomicBoolean isFirst) {
        if (fld.isRedefines()) return;
        for (int k = 0, shift = 0; k < fld.getTimes(); k++, shift += fld.getLength()) {
            int backShift = shift;
            fld.getFields().forEach(it -> validate(it, w, bias - backShift, isFirst));
        }
    }

    @Override
    protected void validateFil(FieldFiller fld, int w, int bias, boolean isFirst) {
        delegateFil.validate(fld, w, bias, isFirst);
    }

    @Override
    protected void validateVal(FieldConstant fld, int w, int bias, boolean isFirst) {
        delegateVal.validate(fld, w, bias, isFirst);
    }

    @Override
    protected void validateNum(FieldNum fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        delegateNum.validate(fld, w, bias, isFirst);
    }

    @Override
    protected void validateAbc(FieldAbc fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        delegateAbc.validate(fld, w, bias, isFirst);
    }

    @Override
    protected void validateDom(FieldDomain fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        delegateDom.validate(fld, w, bias, isFirst);
    }

    protected void validateCus(FieldCustom fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        delegateCus.validate(fld, w, bias, isFirst);
    }

}
