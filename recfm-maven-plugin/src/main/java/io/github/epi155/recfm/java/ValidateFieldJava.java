package io.github.epi155.recfm.java;

import io.github.epi155.recfm.lang.ValidateField;
import io.github.epi155.recfm.type.*;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

class ValidateFieldJava extends ValidateField {

    public ValidateFieldJava(PrintWriter pw, String name, Defaults defaults) {
        super(pw, name, defaults);
    }

    protected void validateGrp(@NotNull FieldGroup fld, int w, int bias, AtomicBoolean firstField) {
        if (fld.isRedefines()) return;
        fld.getFields().forEach(it -> validate(it, w, bias, firstField));
    }

    protected void validateOcc(FieldOccurs fld, int w, int bias, AtomicBoolean firstField) {
        if (fld.isRedefines()) return;
        for (int k = 0, shift = 0; k < fld.getTimes(); k++, shift += fld.getLength()) {
            int backShift = shift;
            fld.getFields().forEach(it -> validate(it, w, bias - backShift, firstField));
        }
    }

    protected void validateFil(FieldFiller fld, int w, int bias, boolean isFirst) {
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

    protected void validateVal(FieldConstant fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        pw.printf("%s checkEqual(%s %5d, %4d, handler, VALUE_AT%dPLUS%d);%n", prefix, fld.pad(-3, w),
            fld.getOffset() - bias, fld.getLength(), fld.getOffset(), fld.getLength());
    }

    protected void validateNum(FieldNum fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        String prefix = prefixOf(isFirst);
        pw.printf("%s checkDigit(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
    }

    protected void validateAbc(FieldAbc fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        String prefix = prefixOf(isFirst);
        switch (fld.getCheck() == null ? defaults.getCheck() : fld.getCheck()) {
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
        }
    }

    @Override
    protected void validateUser(FieldUser fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
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


    private String prefixOf(boolean isFirst) {
        if (isFirst) {
            return "        boolean error =";
        } else {
            return "        error |=";
        }
    }
}
