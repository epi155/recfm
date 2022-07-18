package io.github.epi155.recfm.lang;

import io.github.epi155.recfm.type.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;

@Data @Slf4j
public abstract class PrepareField {
    protected final PrintWriter pw;
    protected final ParentFields struct;

    @SuppressWarnings("StatementWithEmptyBody")
    public void field(NakedField fld, int bias) {
        if (fld instanceof FieldAbc) {
            // nop
        } else if (fld instanceof FieldNum) {
            // nop
        } else if (fld instanceof FieldCustom) {
            // nop
        } else if (fld instanceof FieldDomain) {
            prepareDom((FieldDomain) fld, bias);
        } else if (fld instanceof FieldConstant) {
            prepareVal((FieldConstant) fld, bias);
        } else if (fld instanceof FieldFiller) {
            // nop
        } else if (fld instanceof FieldOccurs) {
            prepareOcc((FieldOccurs) fld, bias);
        } else if (fld instanceof FieldGroup) {
            prepareGrp((FieldGroup) fld, bias);
        } else {
            log.warn("Unknown field type {}", fld.getClass().getSimpleName());
        }
    }

    private void prepareGrp(@NotNull FieldGroup fld, int bias) {
        if (fld.isRedefines()) return;
        fld.getFields().forEach(it -> field(it, bias));
    }

    private void prepareOcc(@NotNull FieldOccurs fld, int bias) {
        if (fld.isRedefines()) return;
        for (int k = 0, shift = 0; k < fld.getTimes(); k++, shift += fld.getLength()) {
            int backShift = shift;
            fld.getFields().forEach(it -> field(it, bias - backShift));
        }
    }

    protected abstract void prepareVal(FieldConstant fld, int bias);

    protected abstract void prepareDom(FieldDomain fld, int bias);
}
