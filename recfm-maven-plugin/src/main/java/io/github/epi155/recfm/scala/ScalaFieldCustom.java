package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.FieldCustom;
import lombok.val;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.function.IntFunction;

public class ScalaFieldCustom extends ActionField<FieldCustom> implements ScalaFieldTools {
    private final String name;

    public ScalaFieldCustom(PrintWriter pw) {
        super(pw);
        this.name = null;
    }

    public ScalaFieldCustom(PrintWriter pw, String name) {
        super(pw);
        this.name = name;
    }

    public ScalaFieldCustom(PrintWriter pw, IntFunction<String> pos, String name) {
        super(pw, pos);
        this.name = name;
    }


    @Override
    public void initialize(@NotNull FieldCustom fld, int bias) {
        printf("    fill(%5d, %4d, '%c')%n", fld.getOffset() - bias, fld.getLength(), fld.getInitChar());
    }

    @Override
    public void validate(@NotNull FieldCustom fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        if (fld.getRegex() != null) {
            printf("%s checkRegex(\"%s\"%s, %5d, %4d, handler, %s.PATTERN_AT%dPLUS%d)%n", prefix,
                fld.getName(), fld.pad(w),
                fld.getOffset() - bias, fld.getLength(),
                name, fld.getOffset(), fld.getLength()
            );
            return;
        }
        switch (fld.getCheck()) {
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
            case Digit:
                printf("%s checkDigit(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
            case DigitOrBlank:
                printf("%s checkDigitBlank(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
                break;
        }
    }

    @Override
    public void access(FieldCustom fld, String wrkName, int indent, GenerateArgs ga) {
        pushIndent(indent);
        printf("  final def %s: String = {%n", fld.getName());
        if (ga.getCheck) chkGetter(fld);
        printf("    abc(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("  }%n");
        defaultOnNull(fld);
        printf("  final def %s_=(s: String): Unit = {%n", fld.getName());
        val align = fld.getAlign();
        if (ga.setCheck) {
            printf("    val r = normalize(s, OverflowAction.%s, UnderflowAction.%s, '%c', '%c', %s, %d)%n",
                fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align),
                fld.getPadChar(), fld.getInitChar(),
                pos.apply(fld.getOffset()), fld.getLength()
                );
            chkSetter(fld);
            printf("    abc(r, %s, %d)%n",
                pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align), fld.getPadChar());
        } else {
            printf("    abc(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '%c', '%s')%n",
                pos.apply(fld.getOffset()), fld.getLength(),
                fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align),
                fld.getPadChar(), fld.getInitChar());
        }
        printf("  }%n");
        popIndent();
    }

    private void chkSetter(FieldCustom fld) {
        if (fld.getRegex() != null) {
            printf("    testRegex(r, %s.PATTERN_AT%sPLUS%d)%n", name, pos.apply(fld.getOffset() + 1), fld.getLength());
            return;
        }
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                printf("    testAscii(r)%n");
                break;
            case Latin1:
                printf("    testLatin(r)%n");
                break;
            case Valid:
                printf("    testValid(r)%n");
                break;
            case Digit:
                printf("    testDigit(r)%n");
                break;
            case DigitOrBlank:
                printf("    testDigitBlank(r)%n");
                break;
        }
    }

    private void chkGetter(FieldCustom fld) {
        if (fld.getRegex() != null) {
            printf("    testRegex(%1$s, %2$d, %3$s.PATTERN_AT%4$sPLUS%2$d)%n",
                pos.apply(fld.getOffset()), fld.getLength(), name, pos.apply(fld.getOffset() + 1));
            return;
        }
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
            case Digit:
                printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case DigitOrBlank:
                printf("    testDigitBlank(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
        }
    }

    @Override
    public void prepare(@NotNull FieldCustom fld, int bias) {
        val regex = fld.getRegex();
        if (regex != null) {
            printf("    private val PATTERN_AT%dPLUS%d = \"%s\".r%n",
                fld.getOffset(), fld.getLength(), StringEscapeUtils.escapeJava(regex));
        }
    }
}
