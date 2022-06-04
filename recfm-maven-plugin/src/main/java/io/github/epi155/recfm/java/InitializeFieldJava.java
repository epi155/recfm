package io.github.epi155.recfm.java;

import io.github.epi155.recfm.lang.InitializeField;
import io.github.epi155.recfm.type.*;
import org.apache.commons.text.StringEscapeUtils;

import java.io.PrintWriter;

class InitializeFieldJava extends InitializeField {
    public InitializeFieldJava(PrintWriter pw, ClassDefine struct, Defaults defaults) {
        super(pw, struct, defaults);
    }

    protected void initializeOcc(FieldOccurs fld, int bias) {
        if (fld.isRedefines()) return;
        for (int k = 0, shift = 0; k < fld.getTimes(); k++, shift += fld.getLength()) {
            int backShift = shift;
            fld.getFields().forEach(it -> field(it, bias - backShift));
        }
    }

    protected void initializeGrp(FieldGroup fld, int bias) {
        if (fld.isRedefines()) return;
        fld.getFields().forEach(it -> field(it, bias));
    }

    protected void initializeFil(FieldFiller fld, int bias) {
        char c = fld.getFillChar() == null ? defaults.getFillChar() : fld.getFillChar();
        pw.printf("        fill(%5d, %4d, '%s');%n",
            fld.getOffset() - bias, fld.getLength(), StringEscapeUtils.escapeJava(String.valueOf(c)));
    }

    protected void initializeVal(FieldConstant fld, int bias) {
        pw.printf("        fill(%5d, %4d, VALUE_AT%dPLUS%d);%n",
            fld.getOffset() - bias, fld.getLength(), fld.getOffset(), fld.getLength());

    }

    protected void initializeNum(FieldNum fld, int bias) {
        if (fld.isRedefines()) return;
        if (fld.getSpace() == SpaceMan.Init) {
            pw.printf("        fill(%5d, %4d, ' ');%n", fld.getOffset() - bias, fld.getLength());
        } else {
            pw.printf("        fill(%5d, %4d, '0');%n", fld.getOffset() - bias, fld.getLength());
        }
    }

    protected void initializeAbc(FieldAbc fld, int bias) {
        if (fld.isRedefines()) return;
        pw.printf("        fill(%5d, %4d, ' ');%n", fld.getOffset() - bias, fld.getLength());
    }
}
