package io.github.epi155.recfm.lang;

import lombok.AllArgsConstructor;

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

    public abstract void initialize(T fld, int bias);

    public abstract void validate(T fld, int w, int bias, boolean isFirst);
}
