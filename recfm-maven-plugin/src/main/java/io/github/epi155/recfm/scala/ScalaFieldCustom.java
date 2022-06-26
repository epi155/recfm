package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.FieldCustom;
import lombok.val;

import java.io.PrintWriter;
import java.util.function.IntFunction;

public class ScalaFieldCustom extends ActionField<FieldCustom> implements ScalaFieldTools {
    public ScalaFieldCustom(PrintWriter pw, IntFunction<String> pos) {
        super(pw, pos);
    }

    public ScalaFieldCustom(PrintWriter pw) {
        super(pw);
    }

    @Override
    public void initialize(FieldCustom fld, int bias) {
        printf("    fill(%5d, %4d, '%c')%n", fld.getOffset() - bias, fld.getLength(), fld.getInitChar());
    }

    @Override
    public void validate(FieldCustom fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                printf("%s checkAscii(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Latin1:
                printf("%s checkLatin(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Valid:
                printf("%s checkValid(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Digit:
                printf("%s checkDigit(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case DigitOrBlank:
                printf("%s checkDigitBlank(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
        }
    }

    @Override
    public void access(FieldCustom fld, String wrkName, int indent, GenerateArgs ga) {
        pushIndent(indent);
        printf("  final def %s: String = {%n", fld.getName());
        if (ga.getCheck) chkGetter(fld);
        printf("    abc(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("  }%n");
        defaultOnNull(fld);
        printf("  final def %s_=(s: String): Unit = {%n", fld.getName());
        if (ga.setCheck) chkSetter(fld);
        val align = fld.getAlign();
        printf("    abc(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '%c', ' ')%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align), fld.getPadChar());
        printf("  }%n");
        popIndent();
    }

    private void chkSetter(FieldCustom fld) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                printf("    testAscii(s)%n");
                break;
            case Latin1:
                printf("    testLatin(s)%n");
                break;
            case Valid:
                printf("    testValid(s)%n");
                break;
            case Digit:
                printf("    testDigit(s)%n");
                break;
            case DigitOrBlank:
                printf("    testDigitBlank(s)%n");
                break;
        }
    }

    private void chkGetter(FieldCustom fld) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                printf("    testAscii(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Latin1:
                printf("    testLatin(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Valid:
                printf("    testValid(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Digit:
                printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case DigitOrBlank:
                printf("    testDigitBlank(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
        }
    }
}
