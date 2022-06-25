package io.github.epi155.recfm.lang;

import io.github.epi155.recfm.exec.GenerateArgs;

import java.io.PrintWriter;
import java.nio.CharBuffer;
import java.util.function.IntFunction;

public abstract class ActionField<T> {
    protected final PrintWriter pw;
    protected final IntFunction<String> pos;

    protected ActionField(PrintWriter pw) {
        this.pw = pw;
        this.pos = String::valueOf;
    }

    protected ActionField(PrintWriter pw, IntFunction<String> pos) {
        this.pw = pw;
        this.pos = pos;
    }

    protected void indent(int indent) {
        pw.write(CharBuffer.allocate(indent).toString().replace('\0', ' '));
    }

    public abstract void initialize(T fld, int bias);

    public abstract void validate(T fld, int w, int bias, boolean isFirst);

    public abstract void access(T fld, String wrkName, int indent, GenerateArgs ga);
}
