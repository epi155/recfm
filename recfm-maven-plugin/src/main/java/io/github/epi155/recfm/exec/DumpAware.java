package io.github.epi155.recfm.exec;

import lombok.AllArgsConstructor;

import java.io.PrintWriter;

@AllArgsConstructor
public abstract class DumpAware {
    public final String name;
    public final int offset;
    public final int length;

    public abstract DumpAware dotFill(int i);

    public abstract void dump(PrintWriter pw);
}
