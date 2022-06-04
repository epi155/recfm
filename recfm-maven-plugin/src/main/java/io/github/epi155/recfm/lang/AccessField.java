package io.github.epi155.recfm.lang;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.type.*;
import lombok.Data;
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
     */
    public void createMethods(SettableField fld, int indent, GenerateArgs ga) {
        if (fld instanceof FieldAbc) {
            createMethodsAbc((FieldAbc) fld, indent, ga.doc);
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
     * @param doc    javadoc required
     */
    protected abstract void createMethodsAbc(FieldAbc fld, int indent, boolean doc);

    /**
     * Set system default on numeric field overflow/underflow
     *
     * @param fld numeric field
     */
    protected void normalizeNum(@NotNull FieldNum fld) {
        if (fld.getOnOverflow() == null) fld.setOnOverflow(OverflowAction.TruncLeft);
        if (fld.getOnUnderflow() == null) fld.setOnUnderflow(UnderflowAction.PadLeft);
    }

    /**
     * Set system default on alphanumeric field overflow/underflow
     *
     * @param fld alphanumeric field
     */
    protected void normalizeAbc(@NotNull FieldAbc fld) {
        if (fld.getOnOverflow() == null) fld.setOnOverflow(OverflowAction.TruncRight);
        if (fld.getOnUnderflow() == null) fld.setOnUnderflow(UnderflowAction.PadRight);
    }
}
