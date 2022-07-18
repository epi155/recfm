package io.github.epi155.recfm.java;

import io.github.epi155.recfm.lang.FieldTools;
import io.github.epi155.recfm.type.NakedField;
import org.jetbrains.annotations.NotNull;

public interface JavaFieldTools extends FieldTools {
    default String prefixOf(boolean isFirst) {
        if (isFirst) {
            return "        boolean error =";
        } else {
            return "        error |=";
        }
    }
    void printf(String format, Object... args);
    default void docSetter(@NotNull NakedField fld) {
        printf("    /**%n");
        printf("     * Use @%d+%d%n", fld.getOffset(), fld.getLength());
        printf("     * @param s string value%n");
        printf("     */%n");
    }

    default void docGetter(@NotNull NakedField fld) {
        printf("    /**%n");
        printf("     * Use @%d+%d%n", fld.getOffset(), fld.getLength());
        printf("     * @return string value%n");
        printf("     */%n");
    }

}
