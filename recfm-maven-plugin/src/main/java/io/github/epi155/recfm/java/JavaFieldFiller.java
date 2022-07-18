package io.github.epi155.recfm.java;

import io.github.epi155.recfm.lang.StemField;
import io.github.epi155.recfm.type.Defaults;
import io.github.epi155.recfm.type.FieldFiller;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;

public class JavaFieldFiller extends StemField<FieldFiller> implements JavaFieldTools {
    private final Defaults defaults;

    public JavaFieldFiller(PrintWriter pw, Defaults defaults) {
        super(pw);
        this.defaults = defaults;
    }

    @Override
    public void initialize(@NotNull FieldFiller fld, int bias) {
        char c = fld.getFillChar() == null ? defaults.getFillChar() : fld.getFillChar();
        printf("        fill(%5d, %4d, '%s');%n",
            fld.getOffset() - bias, fld.getLength(), StringEscapeUtils.escapeJava(String.valueOf(c)));
    }

    @Override
    public void validate(@NotNull FieldFiller fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                printf("%s checkAscii(\"FILLER\"%s, %5d, %4d, handler);%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
            case Latin1:
                printf("%s checkLatin(\"FILLER\"%s, %5d, %4d, handler);%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
            case Valid:
                printf("%s checkValid(\"FILLER\"%s, %5d, %4d, handler);%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
        }
    }

}
