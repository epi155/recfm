package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.FieldNum;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.function.IntFunction;

@Slf4j
public class ScalaFieldNum extends ActionField<FieldNum> implements ScalaFieldTools {
    public ScalaFieldNum(PrintWriter pw, IntFunction<String> pos) {
        super(pw, pos);
    }

    public ScalaFieldNum(PrintWriter pw) {
        super(pw);
    }

    @Override
    public void initialize(@NotNull FieldNum fld, int bias) {
        printf("    fill(%5d, %4d, '0')%n", fld.getOffset() - bias, fld.getLength());
    }

    @Override
    public void validate(@NotNull FieldNum fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        printf("%s checkDigit(\"%s\"%s, %5d, %4d, handler)%n", prefix, fld.getName(), fld.pad(w), fld.getOffset() - bias, fld.getLength());
    }

    @Override
    public void access(FieldNum fld, String wrkName, int indent, GenerateArgs ga) {
        pushIndent(indent);
        numeric(fld);
        if (fld.isNumericAccess()) {
            if (fld.getLength() > 19)
                log.warn("Field {} too large {}-digits for numeric access", fld.getName(), fld.getLength());
            else if (fld.getLength() > 9) useLong(fld, wrkName);    // 10..19
            else if (fld.getLength() > 4 || ga.align == 4) useInt(fld, wrkName);     // 5..9
            else if (fld.getLength() > 2 || ga.align == 2) useShort(fld, wrkName);   // 3..4
            else useByte(fld, wrkName);  // ..2
        }
        popIndent();
    }

    private void numeric(FieldNum fld) {
        printf("  final def %s: String = {%n", fld.getName());
        printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("    abc(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("  }%n");
        defaultOnNull(fld);
        printf("  final def %s_=(s: String): Unit = {%n", fld.getName());
        setNum(fld, true);
    }

    private void setNum(FieldNum fld, boolean doTest) {
        if (doTest) {
            printf("    testDigit(s)%n");
        }
        val align = fld.getAlign();
        printf("    num(s, %s, %d, OverflowAction.%s, UnderflowAction.%s)%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align));
        printf("  }%n");
    }

    private void useByte(FieldNum fld, String wrkName) {
        printf("  final def byte%s: Byte = {%n", wrkName);
        printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("    abc(%s, %d).toByte%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("  }%n");
        printf("  final def %s_=(n: Byte): Unit = {%n", fld.getName());
        fmtNum(fld);
    }

    private void useShort(FieldNum fld, String wrkName) {
        printf("  final def short%s: Short = {%n", wrkName);
        printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("    abc(%s, %d).toShort%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("  }%n");
        printf("  final def %s_=(n: Short): Unit = {%n", fld.getName());
        fmtNum(fld);
    }

    private void useInt(FieldNum fld, String wrkName) {
        printf("  final def int%s: Int = {%n", wrkName);
        printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("    abc(%s, %d).toInt%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("  }%n");
        printf("  final def %s_=(n: Int): Unit = {%n", fld.getName());
        fmtNum(fld);
    }

    private void useLong(FieldNum fld, String wrkName) {
        printf("  final def long%s: Long = {%n", wrkName);
        printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("    abc(%s, %d).toLong%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("  }%n");
        printf("  final def %s_=(n: Long): Unit = {%n", fld.getName());
        fmtNum(fld);
    }

    private void fmtNum(FieldNum fld) {
        printf("    val s = pic9(%d).format(n);%n", fld.getLength());
        setNum(fld, false);
    }
}
