package io.github.epi155.recfm.exec;

public interface DumpFactory {
    DumpPicure newPicture(String name, int offset, int length, String picture);
}
