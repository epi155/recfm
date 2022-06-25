package io.github.epi155.recfm.java;

import io.github.epi155.recfm.lang.FieldTools;

public interface JavaFieldTools extends FieldTools {
    default String prefixOf(boolean isFirst) {
        if (isFirst) {
            return "        boolean error =";
        } else {
            return "        error |=";
        }
    }
}
