package io.github.epi155.recfm.lang;

import io.github.epi155.recfm.exec.GenerateArgs;

import java.io.PrintWriter;
import java.nio.CharBuffer;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.IntFunction;

public abstract class ActionField<T> extends StemField<T> {
    protected final IntFunction<String> pos;
    protected final Deque<String> indentStack = new LinkedList<>();

    protected ActionField(PrintWriter pw) {
        super(pw);
        this.pos = String::valueOf;
    }

    protected ActionField(PrintWriter pw, IntFunction<String> pos) {
        super(pw);
        this.pos = pos;
    }

    protected void pushIndent(int width) {
        String indent = CharBuffer.allocate(width).toString().replace('\0', ' ');
        indentStack.push(indent);
    }

    protected void popIndent() {
        indentStack.pop();
    }

    @Override
    public void printf(String format, Object... args) {
        if (!indentStack.isEmpty()) {
            write(indentStack.peek());
        }
        super.printf(format, args);
    }

    public abstract void access(T fld, String wrkName, int indent, GenerateArgs ga);
}
