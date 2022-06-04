package io.github.epi155.recfm.type;

import lombok.Data;

@Data
public class Defaults {
    private char fillChar = 0;
    private CheckChar check = CheckChar.Ascii;

}
