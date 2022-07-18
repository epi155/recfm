package io.github.epi155.recfm.lang;

import io.github.epi155.recfm.type.FloatingField;
import io.github.epi155.recfm.type.OverflowAction;
import io.github.epi155.recfm.type.UnderflowAction;
import org.jetbrains.annotations.NotNull;

public interface FieldTools {
    default void defaultOnNull(@NotNull FloatingField fld) {
        if (fld.getOnOverflow() == null) fld.setOnOverflow(OverflowAction.Trunc);
        if (fld.getOnUnderflow() == null) fld.setOnUnderflow(UnderflowAction.Pad);
    }
}
