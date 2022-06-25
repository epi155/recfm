package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.FieldCustom;
import lombok.val;

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
    public void initialize(FieldCustom fld, int bias) {
        pw.printf("        fill(%5d, %4d, '%c');%n", fld.getOffset() - bias, fld.getLength(), fld.getInitChar());
    }

    @Override
    public void validate(FieldCustom fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                pw.printf("%s checkAscii(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Latin1:
                pw.printf("%s checkLatin(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Valid:
                pw.printf("%s checkValid(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Digit:
                pw.printf("%s checkDigit(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case DigitOrBlank:
                pw.printf("%s checkDigitBlank(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
        }
    }

    @Override
    public void access(FieldCustom fld, String wrkName, int indent, GenerateArgs ga) {
        if (ga.doc) docGetter(fld, indent);
        indent(indent);
        pw.printf("    public String get%s() {%n", wrkName);
        if (ga.getCheck) chkGetter(fld, indent);
        indent(indent);
        pw.printf("        return getAbc(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("    }%n");
        defaultOnNull(fld);
        if (ga.doc) docSetter(fld, indent);
        indent(indent);
        pw.printf("    public void set%s(String s) {%n", wrkName);
        if (ga.setCheck) chkSetter(fld, indent);
        indent(indent);
        val align = fld.getAlign();
        pw.printf("        setAbc(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '%c', '%c');%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align), fld.getPadChar(), fld.getInitChar());
        indent(indent);
        pw.printf("    }%n");

    }

    private void chkSetter(FieldCustom fld, int indent) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                indent(indent);
                pw.printf("        testAscii(s);%n");
                break;
            case Latin1:
                indent(indent);
                pw.printf("        testLatin(s);%n");
                break;
            case Valid:
                indent(indent);
                pw.printf("        testValid(s);%n");
                break;
            case Digit:
                indent(indent);
                pw.printf("        testDigit(s);%n");
                break;
            case DigitOrBlank:
                indent(indent);
                pw.printf("        testDigitBlank(s);%n");
                break;
        }
    }

    private void chkGetter(FieldCustom fld, int indent) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                indent(indent);
                pw.printf("        testAscii(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Latin1:
                indent(indent);
                pw.printf("        testLatin(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Valid:
                indent(indent);
                pw.printf("        testValid(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Digit:
                indent(indent);
                pw.printf("        testDigit(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case DigitOrBlank:
                indent(indent);
                pw.printf("        testDigitBlank(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
        }
    }

    private void docSetter(FieldCustom fld, int indent) {
        indent(indent);
        pw.printf("    /**%n");
        indent(indent);
        pw.printf("     * Use @%d+%d%n", fld.getOffset(), fld.getLength());
        indent(indent);
        pw.printf("     * @param s string value%n");
        indent(indent);
        pw.printf("     */%n");
    }

    private void docGetter(FieldCustom fld, int indent) {
        indent(indent);
        pw.printf("    /**%n");
        indent(indent);
        pw.printf("     * Use @%d+%d%n", fld.getOffset(), fld.getLength());
        indent(indent);
        pw.printf("     * @return string value%n");
        indent(indent);
        pw.printf("     */%n");
    }
}
