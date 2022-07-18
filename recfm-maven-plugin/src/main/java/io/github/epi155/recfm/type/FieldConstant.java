package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class FieldConstant extends NakedField implements SelfCheck, CheckAware {
    private String value;
    private boolean audit;

    @Override
    public void selfCheck() {
        if (value == null) {
            log.error("Field @{}+{} without required <value>", getOffset(), getLength());
            throw new ClassDefineException("Field @" + getOffset() + "+" + getLength() + " required <value>");
        }
        if (value.length() != getLength()) {
            log.error("Mismatch value length");
            throw new ClassDefineException("Mismatch value length @" + getOffset() + "+" + getLength());
        }
    }

//    @Override
//    public void writeConstant(BiConsumer<String, String> consumer) {
//        String x = String.format("VALUE_AT%dPLUS%d", getOffset(), getLength());
//        String y = String.format("\"%s\"", StringEscapeUtils.escapeJava(getValue()));
//        consumer.accept(x, y);
//    }
}
