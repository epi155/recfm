package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.lang.ValidateField;
import io.github.epi155.recfm.type.*;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

public class ValidateFieldScala extends ValidateField {
    public ValidateFieldScala(PrintWriter pw, String name, Defaults defaults) {
        super(pw, name, defaults);
    }

    @Override
    protected void validateGrp(FieldGroup fld, int w, int bias, AtomicBoolean isFirst) {
        if (fld.isRedefines()) return;
        fld.getFields().forEach(it -> validate(it, w, bias, isFirst));
    }

    @Override
    protected void validateOcc(FieldOccurs fld, int w, int bias, AtomicBoolean isFirst) {
        if (fld.isRedefines()) return;
        for (int k = 0, shift = 0; k < fld.getTimes(); k++, shift += fld.getLength()) {
            int backShift = shift;
            fld.getFields().forEach(it -> validate(it, w, bias - backShift, isFirst));
        }
    }

    @Override
    protected void validateFil(FieldFiller fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        switch (fld.getCheck() == null ? defaults.getCheck() : fld.getCheck()) {
            case None:
                break;
            case Ascii:
                pw.printf("%s checkAscii(\"FILLER\"%s, %5d, %4d, handler)%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
            case Latin1:
                pw.printf("%s checkLatin(\"FILLER\"%s, %5d, %4d, handler)%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
            case Valid:
                pw.printf("%s checkValid(\"FILLER\"%s, %5d, %4d, handler)%n", prefix, fld.pad(6, w), fld.getOffset() - bias, fld.getLength());
                break;
        }
    }

    @Override
    protected void validateVal(FieldConstant fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        pw.printf("%s checkEqual(\"VALUE\"%s, %5d, %4d, handler, %s.VALUE_AT%dPLUS%d)%n", prefix, fld.pad(5, w),
            fld.getOffset() - bias, fld.getLength(), name, fld.getOffset(), fld.getLength());
    }

    @Override
    protected void validateNum(FieldNum fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        String prefix = prefixOf(isFirst);
        pw.printf("%s checkDigit(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
    }

    @Override
    protected void validateAbc(FieldAbc fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        String prefix = prefixOf(isFirst);
        switch (fld.getCheck() == null ? defaults.getCheck() : fld.getCheck()) {
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
        }
    }

    protected void validateUser(FieldUser fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
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
//            case DigitOrBlank:
//                pw.printf("%s checkDigitBlank(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
//                break;
        }
    }

    private String prefixOf(boolean isFirst) {
        if (isFirst) {
            return "    var error =";
        } else {
            return "    error |=";
        }
    }

}
