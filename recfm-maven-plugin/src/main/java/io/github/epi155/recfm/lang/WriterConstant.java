package io.github.epi155.recfm.lang;

import io.github.epi155.recfm.type.ClassDefine;
import lombok.Data;

import java.io.PrintWriter;

@Data
public abstract class WriterConstant {
    protected final PrintWriter pw;
    protected final ClassDefine struct;
}
