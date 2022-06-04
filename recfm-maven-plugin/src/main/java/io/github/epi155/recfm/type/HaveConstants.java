package io.github.epi155.recfm.type;

import org.apache.commons.text.StringEscapeUtils;

import java.util.function.BiConsumer;

public interface HaveConstants {
    default void writeConstant(BiConsumer<String, String> consumer) {
        String x = String.format("VALUE_AT%dPLUS%d", getOffset(), getLength());
        String y = String.format("\"%s\"", StringEscapeUtils.escapeJava(getValue()));
        consumer.accept(x, y);

    }

    String getValue();

    int getLength();

    int getOffset();
}
