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
        pw.printf("    fill(%5d, %4d, '%c')%n", fld.getOffset() - bias, fld.getLength(), fld.getInitChar());
    }

    @Override
    public void validate(FieldCustom fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                pw.printf("%s checkAscii(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Latin1:
                pw.printf("%s checkLatin(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Valid:
                pw.printf("%s checkValid(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Digit:
                pw.printf("%s checkDigit(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case DigitOrBlank:
                pw.printf("%s checkDigitBlank(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
        }

    }

    @Override
    public void access(FieldCustom fld, String wrkName, int indent, GenerateArgs ga) {
        indent(indent);
        pw.printf("  final def %s: String = {%n", fld.getName());
        if (ga.getCheck) chkGetter(fld, indent);
        indent(indent);
        pw.printf("    abc(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("  }%n");
        defaultOnNull(fld);
        indent(indent);
        pw.printf("  final def %s_=(s: String): Unit = {%n", fld.getName());
        if (ga.setCheck) chkSetter(fld, indent);
        indent(indent);
        val align = fld.getAlign();
        pw.printf("    abc(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '%c', ' ')%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align), fld.getPadChar());
        indent(indent);
        pw.printf("  }%n");

    }

    private void chkSetter(FieldCustom fld, int indent) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                indent(indent);
                pw.printf("    testAscii(s);%n");
                break;
            case Latin1:
                indent(indent);
                pw.printf("    testLatin(s);%n");
                break;
            case Valid:
                indent(indent);
                pw.printf("    testValid(s);%n");
                break;
            case Digit:
                indent(indent);
                pw.printf("    testDigit(s);%n");
                break;
            case DigitOrBlank:
                pw.printf("    testDigitBlank(s);%n");
                break;
        }
    }

    private void chkGetter(FieldCustom fld, int indent) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                indent(indent);
                pw.printf("    testAscii(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Latin1:
                indent(indent);
                pw.printf("    testLatin(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Valid:
                indent(indent);
                pw.printf("    testValid(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Digit:
                indent(indent);
                pw.printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case DigitOrBlank:
                pw.printf("    testDigitBlank(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
        }
    }
}
