package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.FieldNum;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.PrintWriter;
import java.util.function.IntFunction;

@Slf4j
public class JavaFieldNum extends ActionField<FieldNum> implements JavaFieldTools {
    public JavaFieldNum(PrintWriter pw, IntFunction<String> pos) {
        super(pw, pos);
    }

    public JavaFieldNum(PrintWriter pw) {
        super(pw);
    }


    @Override
    public void initialize(FieldNum fld, int bias) {
        pw.printf("        fill(%5d, %4d, '0');%n", fld.getOffset() - bias, fld.getLength());
    }

    @Override
    public void validate(FieldNum fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        pw.printf("%s checkDigit(\"%s\"%s, %5d, %4d, handler);%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
    }

    @Override
    public void access(FieldNum fld, String wrkName, int indent, GenerateArgs ga) {
        numeric(fld, wrkName, indent, ga.doc);
        if (fld.isNumericAccess()) {
            if (fld.getLength() > 19)
                log.warn("Field {} too large {}-digits for numeric access", fld.getName(), fld.getLength());
            else if (fld.getLength() > 9) useLong(fld, wrkName, indent, ga.doc);    // 10..19
            else if (fld.getLength() > 4 || ga.align == 4) useInt(fld, wrkName, indent, ga.doc);     // 5..9
            else if (fld.getLength() > 2 || ga.align == 2) useShort(fld, wrkName, indent, ga.doc);   // 3..4
            else useByte(fld, wrkName, indent, ga.doc);  // ..2
        }

    }

    private void numeric(FieldNum fld, String wrkName, int indent, boolean doc) {
        if (doc) docGetter(fld, indent, "string");
        indent(indent);
        pw.printf("    public String get%s() {%n", wrkName);
        indent(indent);
        pw.printf("        testDigit(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("        return getAbc(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("    }%n");
        defaultOnNull(fld);
        indent(indent);
        if (doc) docSetter(fld, indent, "s string");
        pw.printf("    public void set%s(String s) {%n", wrkName);
        setNum(fld, indent, true);
    }

    private void docSetter(FieldNum fld, int indent, String dsResult) {
        indent(indent);
        pw.printf("    /**%n");
        indent(indent);
        pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
        indent(indent);
        pw.printf("     * @param %s value%n", dsResult);
        indent(indent);
        pw.printf("     */%n");
    }

    private void docGetter(FieldNum fld, int indent, String dsType) {
        indent(indent);
        pw.printf("    /**%n");
        indent(indent);
        pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
        indent(indent);
        pw.printf("     * @return %s value%n", dsType);
        indent(indent);
        pw.printf("     */%n");
    }

    private void setNum(FieldNum fld, int indent, boolean doTest) {
        indent(indent);
        if (doTest) {
            indent(indent);
            pw.printf("        testDigit(s);%n");
        }
        val align = fld.getAlign();
        pw.printf("        setNum(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '0');%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align));
        indent(indent);
        pw.printf("    }%n");
    }

    private void useByte(FieldNum fld, String wrkName, int indent, boolean doc) {
        if (doc) docGetter(fld, indent, "byte");
        indent(indent);
        pw.printf("    public byte byte%s() {%n", wrkName);
        indent(indent);
        pw.printf("        testDigit(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("        return Byte.parseByte(getAbc(%s, %d), 10);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("    }%n");
        if (doc) docSetter(fld, indent, "n byte");
        indent(indent);
        pw.printf("    public void set%s(byte n) {%n", wrkName);
        fmtNum(fld, indent);
    }

    private void fmtNum(FieldNum fld, int indent) {
        indent(indent);
        pw.printf("        String s = pic9(%d).format(n);%n", fld.getLength());
        setNum(fld, indent, false);
    }

    private void useShort(FieldNum fld, String wrkName, int indent, boolean doc) {
        if (doc) docGetter(fld, indent, "short");
        indent(indent);
        pw.printf("    public short short%s() {%n", wrkName);
        indent(indent);
        pw.printf("        testDigit(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("        return Short.parseShort(getAbc(%s, %d), 10);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("    }%n");
        if (doc) docSetter(fld, indent, "n short");
        indent(indent);
        pw.printf("    public void set%s(short n) {%n", wrkName);
        fmtNum(fld, indent);
    }

    private void useInt(FieldNum fld, String wrkName, int indent, boolean doc) {
        if (doc) docGetter(fld, indent, "integer");
        indent(indent);
        pw.printf("    public int int%s() {%n", wrkName);
        indent(indent);
        pw.printf("        testDigit(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("        return Integer.parseInt(getAbc(%s, %d), 10);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("    }%n");
        if (doc) docSetter(fld, indent, "n integer");
        indent(indent);
        pw.printf("    public void set%s(int n) {%n", wrkName);
        fmtNum(fld, indent);
    }

    private void useLong(FieldNum fld, String wrkName, int indent, boolean doc) {
        if (doc) docGetter(fld, indent, "long");
        indent(indent);
        pw.printf("    public long long%s() {%n", wrkName);
        indent(indent);
        pw.printf("        testDigit(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("        return Long.parseLong(getAbc(%s, %d), 10);%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(indent);
        pw.printf("    }%n");
        if (doc) docSetter(fld, indent, "n long");
        indent(indent);
        pw.printf("    public void set%s(long n) {%n", wrkName);
        fmtNum(fld, indent);
    }
}
