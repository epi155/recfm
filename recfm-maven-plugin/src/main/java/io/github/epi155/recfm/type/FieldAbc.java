package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FieldAbc extends FloatingField /*implements IndentAble*/ {
    private char padChar = ' ';
    private CheckChar check;

    @Override
    public AlignMode getAlign() {
        return AlignMode.LFT;
    }

    @Override
    public String picture() {
        return "X";
    }
}
