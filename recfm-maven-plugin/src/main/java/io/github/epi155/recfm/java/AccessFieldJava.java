package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.exec.LanguageContext;
import io.github.epi155.recfm.lang.AccessField;
import io.github.epi155.recfm.type.FieldAbc;
import io.github.epi155.recfm.type.FieldNum;
import io.github.epi155.recfm.type.IndentAble;
import io.github.epi155.recfm.type.SpaceMan;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.function.IntFunction;

/**
 * class that generates the java methods for accessing the fields.
 */
@Slf4j
class AccessFieldJava extends AccessField implements IndentAble {
    /**
     * Contructor
     *
     * @param pw  print writer
     * @param pos offset field to string form
     */
    public AccessFieldJava(PrintWriter pw, IntFunction<String> pos) {
        super(pw, pos);
    }

    protected void createMethodsNum(@NotNull FieldNum fld, int indent, @NotNull GenerateArgs ga) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
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

    private void useByte(FieldNum fld, String wrkName, int indent, boolean doc) {
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @return byte value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        indent(pw, indent);
        pw.printf("    public byte byte%s() { return Byte.parseByte(getAbc(%s, %d), 10); }%n",
            wrkName, pos.apply(fld.getOffset()), fld.getLength());
        normalizeNum(fld);
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @param n byte value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        indent(pw, indent);
        pw.printf("    public void set%s(byte n) {%n", wrkName);
        fmtNum(fld, indent);
    }

    private void useShort(FieldNum fld, String wrkName, int indent, boolean doc) {
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @return short value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        indent(pw, indent);
        pw.printf("    public short short%s() { return Short.parseShort(getAbc(%s, %d), 10); }%n",
            wrkName, pos.apply(fld.getOffset()), fld.getLength());
        normalizeNum(fld);
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @param n short value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        indent(pw, indent);
        pw.printf("    public void set%s(short n) {%n", wrkName);
        fmtNum(fld, indent);
    }

    private void useInt(FieldNum fld, String wrkName, int indent, boolean doc) {
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @return integer value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        indent(pw, indent);
        pw.printf("    public int int%s() { return Integer.parseInt(getAbc(%s, %d), 10); }%n",
            wrkName, pos.apply(fld.getOffset()), fld.getLength());
        normalizeNum(fld);
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @param n integer value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        indent(pw, indent);
        pw.printf("    public void set%s(int n) {%n", wrkName);
        fmtNum(fld, indent);
    }

    private void useLong(FieldNum fld, String wrkName, int indent, boolean doc) {
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @return long value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        indent(pw, indent);
        pw.printf("    public long long%s() { return Long.parseLong(getAbc(%s, %d), 10); }%n",
            wrkName, pos.apply(fld.getOffset()), fld.getLength());
        normalizeNum(fld);
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @param n long value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        indent(pw, indent);
        pw.printf("    public void set%s(long n) {%n", wrkName);
        fmtNum(fld, indent);
    }

    private void fmtNum(FieldNum fld, int indent) {
        indent(pw, indent);
        pw.printf("        String s = pic9(%d).format(n);%n", fld.getLength());
        setNum(fld, indent);
    }

    private void numeric(FieldNum fld, String wrkName, int indent, boolean doc) {
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @return string value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        indent(pw, indent);
        if (fld.getSpace() == SpaceMan.Deny) {
            pw.printf("    public String get%s() { return getAbc(%s, %d); }%n",
                wrkName, pos.apply(fld.getOffset()), fld.getLength());
        } else {
            pw.printf("    public String get%s() { return spaceNull(getAbc(%s, %d)); }%n",
                wrkName, pos.apply(fld.getOffset()), fld.getLength());
        }
        normalizeNum(fld);
        indent(pw, indent);
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Num @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @param s string value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        pw.printf("    public void set%s(String s) {%n", wrkName);
        setNum(fld, indent);
    }

    private void setNum(FieldNum fld, int indent) {
        indent(pw, indent);
        if (fld.getSpace() == SpaceMan.Init) {
            pw.printf("        setNum(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, ' ');%n",
                pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow(), fld.getOnUnderflow());
        } else {
            pw.printf("        setNum(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '0');%n",
                pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow(), fld.getOnUnderflow());
        }
        indent(pw, indent);
        pw.printf("    }%n");
    }

    @Override
    protected void createMethodsAbc(@NotNull FieldAbc fld, int indent, boolean doc) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
        alphanumeric(fld, wrkName, indent, doc);
    }

    private void alphanumeric(FieldAbc fld, String wrkName, int indent, boolean doc) {
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Abc @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @return string value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        indent(pw, indent);
        pw.printf("    public String get%s() { return getAbc(%s, %d); }%n",
            wrkName, pos.apply(fld.getOffset()), fld.getLength());
        normalizeAbc(fld);
        if (doc) {
            indent(pw, indent);
            pw.printf("    /**%n");
            indent(pw, indent);
            pw.printf("     * Abc @%d+%d%n", fld.getOffset(), fld.getLength());
            indent(pw, indent);
            pw.printf("     * @param s string value%n");
            indent(pw, indent);
            pw.printf("     */%n");
        }
        indent(pw, indent);
        pw.printf("    public void set%s(String s) {%n", wrkName);
        indent(pw, indent);
        pw.printf("        setAbc(s, %s, %d, OverflowAction.%s, UnderflowAction.%s, '%c');%n",
            pos.apply(fld.getOffset()), fld.getLength(), fld.getOnOverflow(), fld.getOnUnderflow(), fld.getPadChar());
        indent(pw, indent);
        pw.printf("    }%n");
    }
}