package io.github.epi155.recfm.java;

import io.github.epi155.recfm.lang.StemField;
import io.github.epi155.recfm.type.FieldConstant;

import java.io.PrintWriter;

public class JavaFieldConstant extends StemField<FieldConstant> implements JavaFieldTools {
    public JavaFieldConstant(PrintWriter pw) {
        super(pw);
    }

    @Override
    public void initialize(FieldConstant fld, int bias) {
        printf("        fill(%5d, %4d, VALUE_AT%dPLUS%d);%n",
            fld.getOffset() - bias, fld.getLength(), fld.getOffset(), fld.getLength());
    }

    @Override
    public void validate(FieldConstant fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        printf("%s checkEqual(%s %5d, %4d, handler, VALUE_AT%dPLUS%d);%n", prefix, fld.pad(-3, w),
            fld.getOffset() - bias, fld.getLength(), fld.getOffset(), fld.getLength());
    }

}
