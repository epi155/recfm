package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.DumpAware;

import java.io.PrintWriter;

import static io.github.epi155.recfm.java.ContextJava.rpad;

public class DumpJava extends DumpAware {
    public DumpJava(String name, int offset, int length) {
        super(name, offset, length);
    }

    @Override
    public DumpAware dotFill(int w) {
        return new DumpJava(rpad(name, w, '.'), offset, length);
    }

    @Override
    public void dump(PrintWriter pw) {
        pw.printf("        sb.append(\"%s : \").append(dump(%d,%d)).append('\\n');%n", name, offset - 1, length);
    }
}
