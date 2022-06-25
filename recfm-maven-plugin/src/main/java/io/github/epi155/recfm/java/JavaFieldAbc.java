package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.CheckChar;
import io.github.epi155.recfm.type.Defaults;
import io.github.epi155.recfm.type.FieldAbc;
import io.github.epi155.recfm.type.SettableField;
import lombok.val;

import java.io.PrintWriter;
import java.util.function.IntFunction;

public class JavaFieldAbc extends ActionField<FieldAbc> implements JavaFieldTools {
    private final Defaults defaults;

    public JavaFieldAbc(PrintWriter pw, Defaults defaults) {
        super(pw);
        this.defaults = defaults;
    }

    public JavaFieldAbc(PrintWriter pw, IntFunction<String> pos) {
        super(pw, pos);
        this.defaults = null;
    }

    @Override
    public void initialize(FieldAbc fld, int bias) {
        pw.printf("        fill(%5d, %4d, ' ');%n", fld.getOffset() - bias, fld.getLength());
    }

    @Override
    public void validate(FieldAbc fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        CheckChar chk = fld.getCheck();
        if (chk == null && defaults != null) chk = defaults.getCheck();
        if (chk == null) return;
        switch (chk) {
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
    public void access(FieldAbc fld, String wrkName, int indent, GenerateArgs ga) {
        if (ga.doc) docGetter(pw, fld, indent);
        indent(indent);
        pw.printf("    public String get%s() {%n", wrkName);
        if (ga.getCheck) chkGetter(pw, fld, indent);
        indent(indent);
        pw.printf("        return getAbc(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("    }%n");
        defaultOnNull(fld);
        if (ga.doc) docSetter(pw, fld, indent);
        indent(indent);
        pw.printf("    public void set%s(String s) {%n", wrkName);
        if (ga.setCheck) chkSetter(pw, fld, indent);
        indent(indent);
        val align = fld.getAlign();
        pw.printf("        setAbc(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '%c', ' ');%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align), fld.getPadChar());
        indent(indent);
        pw.printf("    }%n");

    }

    private void docGetter(PrintWriter pw, SettableField fld, int indent) {
        indent(indent);
        pw.printf("    /**%n");
        indent(indent);
        pw.printf("     * Abc @%d+%d%n", fld.getOffset(), fld.getLength());
        indent(indent);
        pw.printf("     * @return string value%n");
        indent(indent);
        pw.printf("     */%n");
    }

    private void docSetter(PrintWriter pw, SettableField fld, int indent) {
        indent(indent);
        pw.printf("    /**%n");
        indent(indent);
        pw.printf("     * Abc @%d+%d%n", fld.getOffset(), fld.getLength());
        indent(indent);
        pw.printf("     * @param s string value%n");
        indent(indent);
        pw.printf("     */%n");
    }

    private void chkGetter(PrintWriter pw, FieldAbc fld, int indent) {
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
        }
    }

    private void chkSetter(PrintWriter pw, FieldAbc fld, int indent) {
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
        }
    }

}
