package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.DumpPicure;
import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.exec.LanguageContext;
import io.github.epi155.recfm.type.*;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.function.IntFunction;

public class ActionAbc extends FieldAction<FieldAbc> {
    protected final Defaults defaults;

    public ActionAbc(PrintWriter pw, Defaults defaults) {
        super(pw);
        this.defaults = defaults;
    }

    private static String prefixOf(boolean isFirst) {
        if (isFirst) {
            return "        boolean error =";
        } else {
            return "        error |=";
        }
    }

    private static void defaultOnNull(@NotNull SettableField fld) {
        if (fld.getOnOverflow() == null) fld.setOnOverflow(OverflowAction.Trunc);
        if (fld.getOnUnderflow() == null) fld.setOnUnderflow(UnderflowAction.Pad);
    }

    @Override
    protected void initialize(FieldAbc fld, int bias) {
        if (fld.isRedefines()) return;
        printf("        fill(%5d, %4d, ' ');%n", fld.getOffset() - bias, fld.getLength());
    }

    @Override
    protected void validate(FieldAbc fld, int w, int bias, boolean isFirst) {
        if (fld.isRedefines()) return;
        String prefix = prefixOf(isFirst);
        switch (fld.getCheck() == null ? defaults.getCheck() : fld.getCheck()) {
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
    protected void createMethods(FieldAbc fld, GenerateArgs ga, IntFunction<String> pos) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
        alphanumeric(fld, wrkName, ga, pos);
    }

    @Override
    protected DumpPicure dump(FieldAbc fld) {
        return new JavaPicture(fld.getName(), fld.getOffset(), fld.getLength(), "X");
    }

    private void alphanumeric(FieldAbc fld, String wrkName, GenerateArgs ga, IntFunction<String> pos) {
        if (ga.doc) docGetter(fld);
        printf("    public String get%s() {%n", wrkName);
        if (ga.getCheck) chkGetter(fld, pos);
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
    }

    private void docGetter(SettableField fld) {
        printf("    /**%n");
        printf("     * Abc @%d+%d%n", fld.getOffset(), fld.getLength());
        printf("     * @return string value%n");
        printf("     */%n");
    }

    private void docSetter(SettableField fld) {
        printf("    /**%n");
        printf("     * Abc @%d+%d%n", fld.getOffset(), fld.getLength());
        printf("     * @param s string value%n");
        printf("     */%n");
    }

    private void chkGetter(FieldAbc fld, IntFunction<String> pos) {
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

    private void chkSetter(FieldAbc fld) {
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
