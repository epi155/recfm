package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.Defaults;
import io.github.epi155.recfm.type.FieldFiller;
import org.apache.commons.text.StringEscapeUtils;

import java.io.PrintWriter;

public class JavaFieldFiller extends ActionField<FieldFiller> implements JavaFieldTools {
    private final Defaults defaults;

    public JavaFieldFiller(PrintWriter pw, Defaults defaults) {
        super(pw);
        this.defaults = defaults;
    }

    @Override
    public void initialize(FieldFiller fld, int bias) {
        char c = fld.getFillChar() == null ? defaults.getFillChar() : fld.getFillChar();
        pw.printf("        fill(%5d, %4d, '%s');%n",
            fld.getOffset() - bias, fld.getLength(), StringEscapeUtils.escapeJava(String.valueOf(c)));
    }

    @Override
    public void validate(FieldFiller fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        switch (fld.getCheck() == null ? defaults.getCheck() : fld.getCheck()) {
            case None:
                break;
            case Ascii:
                pw.printf("%s checkAscii(\"FILLER\"%s, %5d, %4d, handler);%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
            case Latin1:
                pw.printf("%s checkLatin(\"FILLER\"%s, %5d, %4d, handler);%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
            case Valid:
                pw.printf("%s checkValid(\"FILLER\"%s, %5d, %4d, handler);%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
        }
    }

    @Override
    public void access(FieldFiller fld, String wrkName, int indent, GenerateArgs ga) {
        // nop
    }
}
