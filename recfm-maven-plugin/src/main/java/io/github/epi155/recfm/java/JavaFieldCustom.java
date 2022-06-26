package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.FieldCustom;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.function.IntFunction;

public class JavaFieldCustom extends ActionField<FieldCustom> implements JavaFieldTools {
    public JavaFieldCustom(PrintWriter pw, IntFunction<String> pos) {
        super(pw, pos);
    }

    public JavaFieldCustom(PrintWriter pw) {
        super(pw);
    }

    @Override
    public void initialize(@NotNull FieldCustom fld, int bias) {
        printf("        fill(%5d, %4d, '%c');%n", fld.getOffset() - bias, fld.getLength(), fld.getInitChar());
    }

    @Override
    public void validate(@NotNull FieldCustom fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                printf("%s checkAscii(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Latin1:
                printf("%s checkLatin(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Valid:
                printf("%s checkValid(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Digit:
                printf("%s checkDigit(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case DigitOrBlank:
                printf("%s checkDigitBlank(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
        }
    }

    @Override
    public void access(FieldCustom fld, String wrkName, int indent, @NotNull GenerateArgs ga) {
        pushIndent(indent);
        if (ga.doc) docGetter(fld);
        printf("    public String get%s() {%n", wrkName);
        if (ga.getCheck) chkGetter(fld);
        printf("        return getAbc(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("    }%n");
        defaultOnNull(fld);
        if (ga.doc) docSetter(fld);
        printf("    public void set%s(String s) {%n", wrkName);
        if (ga.setCheck) chkSetter(fld);
        val align = fld.getAlign();
        printf("        setAbc(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '%c', '%c');%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align), fld.getPadChar(), fld.getInitChar());
        printf("    }%n");

    }

    private void chkSetter(@NotNull FieldCustom fld) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                printf("        testAscii(s);%n");
                break;
            case Latin1:
                printf("        testLatin(s);%n");
                break;
            case Valid:
                printf("        testValid(s);%n");
                break;
            case Digit:
                printf("        testDigit(s);%n");
                break;
            case DigitOrBlank:
                printf("        testDigitBlank(s);%n");
                break;
        }
    }

    private void chkGetter(@NotNull FieldCustom fld) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                printf("        testAscii(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Latin1:
                printf("        testLatin(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Valid:
                printf("        testValid(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Digit:
                printf("        testDigit(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case DigitOrBlank:
                printf("        testDigitBlank(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
        }
    }

    private void docSetter(@NotNull FieldCustom fld) {
        printf("    /**%n");
        printf("     * Use @%d+%d%n", fld.getOffset(), fld.getLength());
        printf("     * @param s string value%n");
        printf("     */%n");
    }

    private void docGetter(@NotNull FieldCustom fld) {
        printf("    /**%n");
        printf("     * Use @%d+%d%n", fld.getOffset(), fld.getLength());
        printf("     * @return string value%n");
        printf("     */%n");
    }
}
