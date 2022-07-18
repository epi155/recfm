package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FieldCustom extends FloatingField /*implements IndentAble*/ {
    private char padChar = ' ';
    private char initChar = ' ';
    private CheckUser check = CheckUser.Ascii;
    private AlignMode align = AlignMode.LFT;

    @Override
    public String picture() {
        return "X";
    }
}
