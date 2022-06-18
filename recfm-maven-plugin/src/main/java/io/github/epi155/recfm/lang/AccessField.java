package io.github.epi155.recfm.lang;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.type.*;
import lombok.Data;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.function.IntFunction;

/**
 * Generic class that generates the methods for accessing the fields.
 */
@Data
public abstract class AccessField {
    /**
     * output writer
     */
    protected final PrintWriter pw;
    /**
     * field offset position
     */
    protected final IntFunction<String> pos;

    /**
     * Method accessor creator
     *
     * @param fld    settable field
     * @param indent code indent
     * @param ga     generator arguments
     * @param check  global check alphanumeric fields
     */
    public void createMethods(SettableField fld, int indent, GenerateArgs ga, CheckChar check) {
        if (fld instanceof FieldAbc) {
            val abc = (FieldAbc) fld;
            if (abc.getCheck() == null) abc.setCheck(check);
            createMethodsAbc(abc, indent, ga);
        } else if (fld instanceof FieldNum) {
            createMethodsNum((FieldNum) fld, indent, ga);
        }
    }

    /**
     * methods for numeric fields
     *
     * @param fld    numeric field
     * @param indent code indent
     * @param ga     option for generate
     */
    protected abstract void createMethodsNum(FieldNum fld, int indent, GenerateArgs ga);

    /**
     * Methods for alphanumeric fields
     *
     * @param fld    alphanumeric field
     * @param indent code indent
     * @param ga     javadoc required
     */
    protected abstract void createMethodsAbc(FieldAbc fld, int indent, GenerateArgs ga);

    /**
     * Set system default on field overflow/underflow
     *
     * @param fld field
     */
    protected void defaultOnNull(@NotNull SettableField fld) {
        if (fld.getOnOverflow() == null) fld.setOnOverflow(OverflowAction.Trunc);
        if (fld.getOnUnderflow() == null) fld.setOnUnderflow(UnderflowAction.Pad);
    }
}
