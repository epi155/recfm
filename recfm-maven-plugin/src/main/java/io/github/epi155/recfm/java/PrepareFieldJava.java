package io.github.epi155.recfm.java;

import io.github.epi155.recfm.lang.PrepareField;
import io.github.epi155.recfm.lang.StemField;
import io.github.epi155.recfm.type.FieldConstant;
import io.github.epi155.recfm.type.FieldDomain;
import io.github.epi155.recfm.type.ParentFields;
import lombok.EqualsAndHashCode;

import java.io.PrintWriter;

@EqualsAndHashCode(callSuper = true)
public class PrepareFieldJava extends PrepareField {
    private final StemField<FieldDomain> delegateDom;
    private final StemField<FieldConstant> delegateVal;

    public PrepareFieldJava(PrintWriter pw, ParentFields struct) {
        super(pw, struct);
        this.delegateDom = new JavaFieldDomain(pw);
        this.delegateVal = new JavaFieldConstant(pw);
    }

    @Override
    protected void prepareVal(FieldConstant fld, int bias) {
        delegateVal.prepare(fld, bias);
    }

    @Override
    protected void prepareDom(FieldDomain fld, int bias) {
        delegateDom.prepare(fld, bias);
    }
}
