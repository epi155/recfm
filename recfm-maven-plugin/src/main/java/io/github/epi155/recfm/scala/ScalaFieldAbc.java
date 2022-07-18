package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.CheckChar;
import io.github.epi155.recfm.type.Defaults;
import io.github.epi155.recfm.type.FieldAbc;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.function.IntFunction;

public class ScalaFieldAbc extends ActionField<FieldAbc> implements ScalaFieldTools {
    private final Defaults defaults;

    public ScalaFieldAbc(PrintWriter pw, IntFunction<String> pos) {
        super(pw, pos);
        this.defaults = null;
    }

    public ScalaFieldAbc(PrintWriter pw, Defaults defaults) {
        super(pw);
        this.defaults = defaults;
    }

    @Override
    public void initialize(@NotNull FieldAbc fld, int bias) {
        printf("    fill(%5d, %4d, ' ')%n", fld.getOffset() - bias, fld.getLength());
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
                printf("%s checkAscii(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Latin1:
                printf("%s checkLatin(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case Valid:
                printf("%s checkValid(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
        }
    }

    @Override
    public void access(FieldAbc fld, String wrkName, int indent, GenerateArgs ga) {
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

    private void chkSetter(FieldAbc fld) {
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
        }
    }

    private void chkGetter(FieldAbc fld) {
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
        }
    }
}
