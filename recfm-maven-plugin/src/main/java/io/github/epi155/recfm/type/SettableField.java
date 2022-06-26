package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class SettableField extends NamedField {
    private OverflowAction onOverflow;
    private UnderflowAction onUnderflow;

    public abstract AlignMode getAlign();

    public abstract String picure();

    public String pad(int w) {
        return pad(getName().length(), w);
    }
}
