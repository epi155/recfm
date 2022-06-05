package io.github.epi155.recfm.scala;


import io.github.epi155.recfm.exec.DumpFactory;
import io.github.epi155.recfm.exec.DumpPicure;

public class DumpFactoryScala implements DumpFactory {
    private DumpFactoryScala() {
    }

    public static DumpFactory getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public DumpPicure newPicture(String name, int offset, int length, String picture) {
        return new ScalaPicture(name, offset, length, picture);
    }

    private static class Singleton {
        private static final DumpFactory INSTANCE = new DumpFactoryScala();
    }
}
