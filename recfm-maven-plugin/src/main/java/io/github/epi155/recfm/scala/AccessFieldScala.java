package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.exec.LanguageContext;
import io.github.epi155.recfm.lang.AccessField;
import io.github.epi155.recfm.type.FieldAbc;
import io.github.epi155.recfm.type.FieldNum;
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
            else useInt(fld, wrkName, indent);     // ..9
//            else if (fld.getLength() > 4) useInt(fld, wrkName, indent);     // 5..9
//            else if (fld.getLength() > 2) useShort(fld, wrkName, indent);   // 3..4
//            else useByte(fld, wrkName, indent);  // ..2
        }
//        pw.println();
    }

    private void useByte(FieldNum fld, String wrkName, int indent) {
        indent(pw, indent);
        pw.printf("  final def byte%s: Int = { abc(%s, %d).toByte; }%n",
            wrkName, pos.apply(fld.getOffset()), fld.getLength());
        normalizeNum(fld);
        indent(pw, indent);
        pw.printf("  final def %s_=(n: Byte): Unit = {%n", fld.getName());
        fmtNum(fld, indent);
    }

    private void useShort(FieldNum fld, String wrkName, int indent) {
        indent(pw, indent);
        pw.printf("  final def short%s: Int = { abc(%s, %d).toShort; }%n",
            wrkName, pos.apply(fld.getOffset()), fld.getLength());
        normalizeNum(fld);
        indent(pw, indent);
        pw.printf("  final def %s_=(n: Short): Unit = {%n", fld.getName());
        fmtNum(fld, indent);
    }

    private void useInt(FieldNum fld, String wrkName, int indent) {
        indent(pw, indent);
        pw.printf("  final def int%s: Int = { abc(%s, %d).toInt; }%n",
            wrkName, pos.apply(fld.getOffset()), fld.getLength());
        normalizeNum(fld);
        indent(pw, indent);
        pw.printf("  final def %s_=(n: Int): Unit = {%n", fld.getName());
        fmtNum(fld, indent);
    }

    private void useLong(FieldNum fld, String wrkName, int indent) {
        indent(pw, indent);
        pw.printf("  final def long%s: Long = { abc(%s, %d).toLong; }%n",
            wrkName, pos.apply(fld.getOffset()), fld.getLength());
        normalizeNum(fld);
        indent(pw, indent);
        pw.printf("  final def %s_=(n: Long): Unit = {%n", fld.getName());
        fmtNum(fld, indent);
    }

    private void fmtNum(FieldNum fld, int indent) {
        indent(pw, indent);
        pw.printf("    val s = pic9(%d).format(n);%n", fld.getLength());
        setNum(fld, indent);
    }

//    final def totDetailRecord(): String = abc(24, 9)
//    final def totDetailRecord_=(s: String): Unit = {
//        num(s, 24, 9, OverflowAction.TruncLeft, UnderflowAction.PadLeft)
//    }

    private void numeric(FieldNum fld, int indent) {
        indent(pw, indent);
        pw.printf("  final def %s: String = { abc(%s, %d); }%n",
            fld.getName(), pos.apply(fld.getOffset()), fld.getLength());
        normalizeNum(fld);
        indent(pw, indent);
        pw.printf("  final def %s_=(s: String): Unit = {%n", fld.getName());
        indent(pw, indent);
        setNum(fld, indent);
    }

    private void setNum(FieldNum fld, int indent) {
        indent(pw, indent);
        pw.printf("    num(s, %s, %d, OverflowAction.%s, UnderflowAction.%s)%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow(), fld.getOnUnderflow());
        indent(pw, indent);
        pw.printf("  }%n");
    }

//    final def batchId(): String = abc(10, 14)
//    final def batchId_=(s: String): Unit = {
//        abc(s, 10, 14, OverflowAction.TruncRight, UnderflowAction.PadRight, ' ')
//    }

    @Override
    protected void createMethodsAbc(FieldAbc fld, int indent, boolean doc) {
        indent(pw, indent);
        pw.printf("  final def %s: String = { abc(%s, %d); }%n",
            fld.getName(), pos.apply(fld.getOffset()), fld.getLength());
        normalizeAbc(fld);
        indent(pw, indent);
        pw.printf("  final def %s_=(s: String): Unit = {%n", fld.getName());
        indent(pw, indent);
        pw.printf("    abc(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '%c')%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow(), fld.getOnUnderflow(), fld.getPadChar());
        indent(pw, indent);
        pw.printf("  }%n");
    }
}
