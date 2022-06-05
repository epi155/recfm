package io.github.epi155.recfm.exec;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class DumpPicure {
    public final String name;
    public final int offset;
    public final int length;
    public final String picture;

    protected String picName() {
        return name + ": " + picture + "(" + length + ")@" + offset;
    }


    public abstract DumpAware normalize();
}
