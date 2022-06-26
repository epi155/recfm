package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.CheckChar;
import io.github.epi155.recfm.type.Defaults;
import io.github.epi155.recfm.type.FieldAbc;
import io.github.epi155.recfm.type.SettableField;
import lombok.val;
import org.jetbrains.annotations.NotNull;

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
    public void initialize(@NotNull FieldAbc fld, int bias) {
        printf("        fill(%5d, %4d, ' ');%n", fld.getOffset() - bias, fld.getLength());
    }

    @Override
    public void validate(@NotNull FieldAbc fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        CheckChar chk = fld.getCheck();
        if (chk == null && defaults != null) chk = defaults.getCheck();
        if (chk == null) return;
        switch (chk) {
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
        }
    }

    @Override
    public void access(FieldAbc fld, String wrkName, int indent, @NotNull GenerateArgs ga) {
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
        printf("        setAbc(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '%c', ' ');%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align), fld.getPadChar());
        printf("    }%n");
        popIndent();
    }


    private void docGetter(@NotNull SettableField fld) {
        printf("    /**%n");
        printf("     * Abc @%d+%d%n", fld.getOffset(), fld.getLength());
        printf("     * @return string value%n");
        printf("     */%n");
    }

    private void docSetter(@NotNull SettableField fld) {
        printf("    /**%n");
        printf("     * Abc @%d+%d%n", fld.getOffset(), fld.getLength());
        printf("     * @param s string value%n");
        printf("     */%n");
    }

    private void chkGetter(@NotNull FieldAbc fld) {
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
        }
    }

    private void chkSetter(@NotNull FieldAbc fld) {
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
        }
    }

}
