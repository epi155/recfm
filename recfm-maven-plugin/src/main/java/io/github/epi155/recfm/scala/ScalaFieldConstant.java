package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.lang.StemField;
import io.github.epi155.recfm.type.FieldConstant;

import java.io.PrintWriter;

public class ScalaFieldConstant extends StemField<FieldConstant> implements ScalaFieldTools {
    private final String name;

    public ScalaFieldConstant(PrintWriter pw, String name) {
        super(pw);
        this.name = name;
    }

    @Override
    public void initialize(FieldConstant fld, int bias) {
        printf("    fill(%5d, %4d, %s.VALUE_AT%dPLUS%d)%n",
            fld.getOffset() - bias, fld.getLength(), name, fld.getOffset(), fld.getLength());
    }

    @Override
    public void validate(FieldConstant fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        printf("%s checkEqual(\"VALUE\"%s, %5d, %4d, handler, %s.VALUE_AT%dPLUS%d)%n", prefix, fld.pad(5, w),
            fld.getOffset() - bias, fld.getLength(), name, fld.getOffset(), fld.getLength());
    }

}
