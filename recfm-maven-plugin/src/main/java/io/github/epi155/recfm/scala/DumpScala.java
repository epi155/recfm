package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.exec.DumpAware;

import java.io.PrintWriter;

import static io.github.epi155.recfm.java.ContextJava.rpad;

public class DumpScala extends DumpAware {
    public DumpScala(String name, int offset, int length) {
        super(name, offset, length);
    }

    @Override
    public DumpAware dotFill(int w) {
        return new DumpScala(rpad(name, w, '.'), offset, length);
    }

    @Override
    public void dump(PrintWriter pw) {
        pw.printf("    sb ++= \"%s : \" ++= dump(%d,%d) ++= System.lineSeparator%n", name, offset - 1, length);
    }
}
