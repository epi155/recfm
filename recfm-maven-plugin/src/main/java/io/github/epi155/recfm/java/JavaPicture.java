package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.DumpAware;
import io.github.epi155.recfm.exec.DumpPicure;

public class JavaPicture extends DumpPicure {
    public JavaPicture(String name, int offset, int length, String picture) {
        super(name, offset, length, picture);
    }

    @Override
    public DumpAware normalize() {
        return new DumpJava(picName(), offset, length);
    }

}
