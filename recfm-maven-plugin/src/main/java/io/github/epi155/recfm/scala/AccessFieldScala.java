package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.exec.LanguageContext;
import io.github.epi155.recfm.lang.AccessField;
import io.github.epi155.recfm.type.FieldAbc;
import io.github.epi155.recfm.type.FieldNum;
import io.github.epi155.recfm.type.FieldUser;
import io.github.epi155.recfm.type.IndentAble;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.function.IntFunction;

/**
 * class that generates methods for accessing the fields in scala language.
 */
@Slf4j
public class AccessFieldScala extends AccessField implements IndentAble {
    /**
     * Constructor
     *
     * @param pw  output writer
     * @param pos field offset to string form
     */
    public AccessFieldScala(PrintWriter pw, IntFunction<String> pos) {
        super(pw, pos);
    }

    @Override
    protected void createMethodsNum(@NotNull FieldNum fld, int indent, GenerateArgs ga) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
        numeric(fld, indent);
        if (fld.isNumericAccess()) {
            if (fld.getLength() > 19)
                log.warn("Field {} too large {}-digits for numeric access", fld.getName(), fld.getLength());
            else if (fld.getLength() > 9) useLong(fld, wrkName, indent);    // 10..19
            else if (fld.getLength() > 4 || ga.align == 4) useInt(fld, wrkName, indent);     // 5..9
            else if (fld.getLength() > 2 || ga.align == 2) useShort(fld, wrkName, indent);   // 3..4
            else useByte(fld, wrkName, indent);  // ..2
        }
    }

    private void useByte(FieldNum fld, String wrkName, int indent) {
        indent(pw, indent);
        pw.printf("  final def byte%s: Byte = {%n", wrkName);
        indent(pw, indent);
        pw.printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("    abc(%s, %d).toByte%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("  }%n");
        indent(pw, indent);
        pw.printf("  final def %s_=(n: Byte): Unit = {%n", fld.getName());
        fmtNum(fld, indent);
    }

    private void useShort(FieldNum fld, String wrkName, int indent) {
        indent(pw, indent);
        pw.printf("  final def short%s: Short = {%n", wrkName);
        indent(pw, indent);
        pw.printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("    abc(%s, %d).toShort%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("  }%n");
        indent(pw, indent);
        pw.printf("  final def %s_=(n: Short): Unit = {%n", fld.getName());
        fmtNum(fld, indent);
    }

    private void useInt(FieldNum fld, String wrkName, int indent) {
        indent(pw, indent);
        pw.printf("  final def int%s: Int = {%n", wrkName);
        indent(pw, indent);
        pw.printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("    abc(%s, %d).toInt%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("  }%n");
        indent(pw, indent);
        pw.printf("  final def %s_=(n: Int): Unit = {%n", fld.getName());
        fmtNum(fld, indent);
    }

    private void useLong(FieldNum fld, String wrkName, int indent) {
        indent(pw, indent);
        pw.printf("  final def long%s: Long = {%n", wrkName);
        indent(pw, indent);
        pw.printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("    abc(%s, %d).toLong%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("  }%n");
        indent(pw, indent);
        pw.printf("  final def %s_=(n: Long): Unit = {%n", fld.getName());
        fmtNum(fld, indent);
    }

    private void fmtNum(FieldNum fld, int indent) {
        indent(pw, indent);
        pw.printf("    val s = pic9(%d).format(n);%n", fld.getLength());
        setNum(fld, indent, false);
    }

//    final def totDetailRecord(): String = abc(24, 9)
//    final def totDetailRecord_=(s: String): Unit = {
//        num(s, 24, 9, OverflowAction.TruncLeft, UnderflowAction.PadLeft)
//    }

    private void numeric(FieldNum fld, int indent) {
        indent(pw, indent);
        pw.printf("  final def %s: String = {%n", fld.getName());
        indent(pw, indent);
        pw.printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("    abc(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("  }%n");
        defaultOnNull(fld);
        indent(pw, indent);
        pw.printf("  final def %s_=(s: String): Unit = {%n", fld.getName());
        indent(pw, indent);
        setNum(fld, indent, true);
    }

    private void setNum(FieldNum fld, int indent, boolean doTest) {
        if (doTest) {
            indent(pw, indent);
            pw.printf("    testDigit(s)%n");
        }
        indent(pw, indent);
        val align = fld.getAlign();
        pw.printf("    num(s, %s, %d, OverflowAction.%s, UnderflowAction.%s)%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align));
        indent(pw, indent);
        pw.printf("  }%n");
    }

//    final def batchId(): String = abc(10, 14)
//    final def batchId_=(s: String): Unit = {
//        abc(s, 10, 14, OverflowAction.TruncRight, UnderflowAction.PadRight, ' ')
//    }

    @Override
    protected void createMethodsAbc(FieldAbc fld, int indent, GenerateArgs ga) {
        indent(pw, indent);
        pw.printf("  final def %s: String = {%n", fld.getName());
        if (ga.getCheck) chkGetter(pw, fld, indent);
        indent(pw, indent);
        pw.printf("    abc(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("  }%n");
        defaultOnNull(fld);
        indent(pw, indent);
        pw.printf("  final def %s_=(s: String): Unit = {%n", fld.getName());
        if (ga.setCheck) chkSetter(pw, fld, indent);
        indent(pw, indent);
        val align = fld.getAlign();
        pw.printf("    abc(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '%c', ' ')%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align), fld.getPadChar());
        indent(pw, indent);
        pw.printf("  }%n");
    }

    @Override
    protected void createMethodsUser(FieldUser fld, int indent, GenerateArgs ga) {
        indent(pw, indent);
        pw.printf("  final def %s: String = {%n", fld.getName());
        if (ga.getCheck) chkGetter(pw, fld, indent);
        indent(pw, indent);
        pw.printf("    abc(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        indent(pw, indent);
        pw.printf("  }%n");
        defaultOnNull(fld);
        indent(pw, indent);
        pw.printf("  final def %s_=(s: String): Unit = {%n", fld.getName());
        if (ga.setCheck) chkSetter(pw, fld, indent);
        indent(pw, indent);
        val align = fld.getAlign();
        pw.printf("    abc(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '%c', ' ')%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow().of(align), fld.getOnUnderflow().of(align), fld.getPadChar());
        indent(pw, indent);
        pw.printf("  }%n");
    }

    private void chkSetter(PrintWriter pw, FieldUser fld, int indent) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                indent(pw, indent);
                pw.printf("    testAscii(s);%n");
                break;
            case Latin1:
                indent(pw, indent);
                pw.printf("    testLatin(s);%n");
                break;
            case Valid:
                indent(pw, indent);
                pw.printf("    testValid(s);%n");
                break;
            case Digit:
                indent(pw, indent);
                pw.printf("    testDigit(s);%n");
                break;
//            case DigitOrBlank:
//                break;
        }
    }

    private void chkGetter(PrintWriter pw, FieldUser fld, int indent) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                indent(pw, indent);
                pw.printf("    testAscii(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Latin1:
                indent(pw, indent);
                pw.printf("    testLatin(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Valid:
                indent(pw, indent);
                pw.printf("    testValid(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Digit:
                indent(pw, indent);
                pw.printf("    testDigit(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
//            case DigitOrBlank:
//                break;
        }
    }

    private void chkGetter(PrintWriter pw, FieldAbc fld, int indent) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                indent(pw, indent);
                pw.printf("    testAscii(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Latin1:
                indent(pw, indent);
                pw.printf("    testLatin(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
            case Valid:
                indent(pw, indent);
                pw.printf("    testValid(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
                break;
        }
    }

    private void chkSetter(PrintWriter pw, FieldAbc fld, int indent) {
        switch (fld.getCheck()) {
            case None:
                break;
            case Ascii:
                indent(pw, indent);
                pw.printf("    testAscii(s);%n");
                break;
            case Latin1:
                indent(pw, indent);
                pw.printf("    testLatin(s);%n");
                break;
            case Valid:
                indent(pw, indent);
                pw.printf("    testValid(s);%n");
                break;
        }
    }
}
