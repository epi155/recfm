package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class FloatingField extends SettableField {
    private OverflowAction onOverflow;
    private UnderflowAction onUnderflow;

    public abstract AlignMode getAlign();
}
