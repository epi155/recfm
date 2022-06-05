package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.exec.DumpAware;
import io.github.epi155.recfm.exec.DumpPicure;

public class ScalaPicture extends DumpPicure {
    public ScalaPicture(String name, int offset, int length, String picture) {
        super(name, offset, length, picture);
    }

    @Override
    public DumpAware normalize() {
        return new DumpScala(picName(), offset, length);
    }
}
