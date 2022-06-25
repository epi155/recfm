package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FieldUser extends SettableField implements IndentAble {
    private char padChar = ' ';
    private char initChar = ' ';
    private CheckUser check = CheckUser.Ascii;
    private AlignMode align = AlignMode.Left;
}
