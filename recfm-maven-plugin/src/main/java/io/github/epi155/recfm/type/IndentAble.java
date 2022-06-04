package io.github.epi155.recfm.type;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.nio.CharBuffer;

public interface IndentAble {
    default void indent(@NotNull PrintWriter pw, int indent) {
        pw.write(CharBuffer.allocate(indent).toString().replace('\0', ' '));
    }
}
