package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FieldAbc extends SettableField implements IndentAble {
    private char padChar = ' ';
    private CheckChar check;

    @Override
    public AlignMode align() {
        return AlignMode.Right;
    }
}
