package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.lang.InitializeField;
import io.github.epi155.recfm.type.*;
import lombok.EqualsAndHashCode;

import java.io.PrintWriter;

@EqualsAndHashCode(callSuper = true)
public class InitializeFieldScala extends InitializeField {
    private final ActionField<FieldAbc> delegateAbc;
    private final ActionField<FieldNum> delegateNum;
    private final ActionField<FieldCustom> delegateUse;
    private final ActionField<FieldFiller> delegateFil;
    private final ActionField<FieldConstant> delegateVal;

    public InitializeFieldScala(PrintWriter pw, ClassDefine struct, Defaults defaults) {
        super(pw, struct, defaults);
        this.delegateAbc = new ScalaFieldAbc(pw, defaults);
        this.delegateNum = new ScalaFieldNum(pw);
        this.delegateUse = new ScalaFieldCustom(pw);
        this.delegateFil = new ScalaFieldFiller(pw, defaults);
        this.delegateVal = new ScalaFieldConstant(pw, struct.getName());
    }

    @Override
    protected void initializeUser(FieldCustom fld, int bias) {
        if (fld.isRedefines()) return;
        delegateUse.initialize(fld, bias);
    }

    @Override
    protected void initializeGrp(FieldGroup fld, int bias) {
        if (fld.isRedefines()) return;
        fld.getFields().forEach(it -> field(it, bias));
    }

    @Override
    protected void initializeOcc(FieldOccurs fld, int bias) {
        if (fld.isRedefines()) return;
        for (int k = 0, shift = 0; k < fld.getTimes(); k++, shift += fld.getLength()) {
            int backShift = shift;
            fld.getFields().forEach(it -> field(it, bias - backShift));
        }
    }

    @Override
    protected void initializeFil(FieldFiller fld, int bias) {
        delegateFil.initialize(fld, bias);
    }

    @Override
    protected void initializeVal(FieldConstant fld, int bias) {
        delegateVal.initialize(fld, bias);
    }

    @Override
    protected void initializeNum(FieldNum fld, int bias) {
        if (fld.isRedefines()) return;
        delegateNum.initialize(fld, bias);
    }

    @Override
    protected void initializeAbc(FieldAbc fld, int bias) {
        if (fld.isRedefines()) return;
        delegateAbc.initialize(fld, bias);
    }
}
