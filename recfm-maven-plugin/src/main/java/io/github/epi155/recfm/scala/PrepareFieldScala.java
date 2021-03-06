package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.lang.PrepareField;
import io.github.epi155.recfm.lang.StemField;
import io.github.epi155.recfm.type.FieldConstant;
import io.github.epi155.recfm.type.FieldCustom;
import io.github.epi155.recfm.type.FieldDomain;
import io.github.epi155.recfm.type.ParentFields;
import lombok.EqualsAndHashCode;

import java.io.PrintWriter;

@EqualsAndHashCode(callSuper = true)
public class PrepareFieldScala extends PrepareField {
    private final StemField<FieldDomain> delegateDom;
    private final StemField<FieldConstant> delegateVal;
    private final StemField<FieldCustom> delegateCus;

    public PrepareFieldScala(PrintWriter pw, ParentFields struct) {
        super(pw, struct);
        this.delegateDom = new ScalaFieldDomain(pw);
        this.delegateVal = new ScalaFieldConstant(pw, struct.getName());
        this.delegateCus = new ScalaFieldCustom(pw);
    }

    @Override
    protected void prepareVal(FieldConstant fld, int bias) {
        delegateVal.prepare(fld, bias);
    }

    @Override
    protected void prepareDom(FieldDomain fld, int bias) {
        delegateDom.prepare(fld, bias);
    }

    @Override
    protected void prepareCus(FieldCustom fld, int bias) {
        delegateCus.prepare(fld, bias);
    }
}
