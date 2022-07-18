package io.github.epi155.recfm.lang;

import io.github.epi155.recfm.type.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;

@Data
@Slf4j
public abstract class InitializeField {
    protected final PrintWriter pw;
    protected final ClassDefine struct;
    protected final Defaults defaults;

    public void field(NakedField fld, int bias) {
        if (fld instanceof FieldAbc) {
            initializeAbc((FieldAbc) fld, bias);
        } else if (fld instanceof FieldNum) {
            initializeNum((FieldNum) fld, bias);
        } else if (fld instanceof FieldCustom) {
            initializeCus((FieldCustom) fld, bias);
        } else if (fld instanceof FieldDomain) {
            initializeDom((FieldDomain) fld, bias);
        } else if (fld instanceof FieldConstant) {
            initializeVal((FieldConstant) fld, bias);
        } else if (fld instanceof FieldFiller) {
            initializeFil((FieldFiller) fld, bias);
        } else if (fld instanceof FieldOccurs) {
            initializeOcc((FieldOccurs) fld, bias);
        } else if (fld instanceof FieldGroup) {
            initializeGrp((FieldGroup) fld, bias);
        } else {
            log.warn("Unknown field type {}", fld.getClass().getSimpleName());
        }
    }

    protected abstract void initializeDom(FieldDomain fld, int bias);

    protected abstract void initializeCus(FieldCustom fld, int bias);

    protected abstract void initializeGrp(FieldGroup fld, int bias);

    protected abstract void initializeOcc(FieldOccurs fld, int bias);

    protected abstract void initializeFil(FieldFiller fld, int bias);

    protected abstract void initializeVal(FieldConstant fld, int bias);

    protected abstract void initializeNum(FieldNum fld, int bias);

    protected abstract void initializeAbc(FieldAbc fld, int bias);
}
