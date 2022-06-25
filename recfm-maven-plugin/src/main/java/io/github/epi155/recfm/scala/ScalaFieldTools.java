package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.lang.FieldTools;

public interface ScalaFieldTools extends FieldTools {
    default String prefixOf(boolean isFirst) {
        if (isFirst) {
            return "    var error =";
        } else {
            return "    error |=";
        }
    }
}
