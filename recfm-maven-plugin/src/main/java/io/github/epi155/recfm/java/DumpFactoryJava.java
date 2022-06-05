package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.DumpFactory;
import io.github.epi155.recfm.exec.DumpPicure;

public class DumpFactoryJava implements DumpFactory {
    private DumpFactoryJava() {
    }

    public static DumpFactory getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public DumpPicure newPicture(String name, int offset, int length, String picture) {
        return new JavaPicture(name, offset, length, picture);
    }

    private static class Singleton {
        private static final DumpFactory INSTANCE = new DumpFactoryJava();
    }
}
