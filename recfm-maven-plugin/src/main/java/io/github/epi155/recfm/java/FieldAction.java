package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.DumpPicure;
import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.type.NakedField;
import lombok.Data;

import java.io.PrintWriter;
import java.nio.CharBuffer;
import java.util.function.IntFunction;

@Data
public abstract class FieldAction<T extends NakedField> {
    private static final int STEP = 4;
    private final PrintWriter pw;
    private int indent = 0;
    private boolean newLine = true;

    protected void printf(String format, Object... args) {
        if (newLine && indent > 0) {
            pw.write(CharBuffer.allocate(indent).toString().replace('\0', ' '));
            newLine = false;
        }
        pw.printf(format, args);
        if (format.endsWith("{%n")) {
            indent += STEP;
            newLine = true;
        } else if (format.endsWith("}%n")) {
            indent -= STEP;
            newLine = true;
        } else if (format.endsWith("%n")) {
            newLine = true;
        }
    }

    protected abstract void initialize(T fld, int bias);

    protected abstract void validate(T fld, int padWidth, int bias, boolean isFirst);

    protected abstract void createMethods(T fld, GenerateArgs ga, IntFunction<String> pos);

    protected abstract DumpPicure dump(T fld);
}
