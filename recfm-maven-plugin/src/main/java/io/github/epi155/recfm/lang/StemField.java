package io.github.epi155.recfm.lang;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;

@AllArgsConstructor
public abstract class StemField<T> {
    private final PrintWriter pw;

    public void printf(String format, Object... args) {
        pw.printf(format, args);
    }

    protected void write(String s) {
        pw.write(s);
    }

    public abstract void initialize(@NotNull T fld, int bias);
    public void prepare(@NotNull T fld, int bias) { /* NOP */ }

    public abstract void validate(@NotNull T fld, int w, int bias, boolean isFirst);
}
