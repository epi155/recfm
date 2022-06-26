package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.lang.StemField;
import io.github.epi155.recfm.type.CheckChar;
import io.github.epi155.recfm.type.Defaults;
import io.github.epi155.recfm.type.FieldFiller;
import org.apache.commons.text.StringEscapeUtils;

import java.io.PrintWriter;

public class ScalaFieldFiller extends StemField<FieldFiller> implements ScalaFieldTools {
    private final Defaults defaults;

    public ScalaFieldFiller(PrintWriter pw, Defaults defaults) {
        super(pw);
        this.defaults = defaults;
    }

    @Override
    public void initialize(FieldFiller fld, int bias) {
        char c = fld.getFillChar() == null ? defaults.getFillChar() : fld.getFillChar();
        printf("    fill(%5d, %4d, '%s')%n",
            fld.getOffset() - bias, fld.getLength(), StringEscapeUtils.escapeJava(String.valueOf(c)));
    }

    @Override
    public void validate(FieldFiller fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        CheckChar chk = fld.getCheck();
        if (chk == null && defaults != null) chk = defaults.getCheck();
        if (chk == null) return;
        switch (chk) {
            case None:
                break;
            case Ascii:
                printf("%s checkAscii(\"FILLER\"%s, %5d, %4d, handler)%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
            case Latin1:
                printf("%s checkLatin(\"FILLER\"%s, %5d, %4d, handler)%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
            case Valid:
                printf("%s checkValid(\"FILLER\"%s, %5d, %4d, handler)%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
        }
    }

}
