package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class FieldNum extends FloatingField /*implements IndentAble*/ {
    private boolean numericAccess;

    @Override
    public AlignMode getAlign() {
        return AlignMode.RGT;
    }

    @Override
    public String picture() {
        return "9";
    }
}
