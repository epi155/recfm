package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class FieldDomain extends SettableField implements SelfCheck {
    private String[] items;

    @Override
    public String picture() {
        return "X";
    }

    @Override
    public void selfCheck() {
        // items is required
        if (items == null) {
            log.error("Field {}@{}+{} without required <items>", getName(), getOffset(), getLength());
            throw new ClassDefineException(fieldDescriptor() + " required <items>");
        }
        // items must have at least one value
        if (items.length == 0) {
            log.error("Field {}@{}+{} <items> must have at least one value", getName(), getOffset(), getLength());
            throw new ClassDefineException(fieldDescriptor() + " <items> must have at least one value");
        }
        for (final String value : items) {
            if (value == null) {
                log.error("Field {}@{}+{} <items> values cannot be null", getName(), getOffset(), getLength());
                throw new ClassDefineException(fieldDescriptor() + " <items> values cannot be null");
            }
            if (value.length() != getLength()) {
                log.error("Field {}@{}+{} <items> values must match field length", getName(), getOffset(), getLength());
                throw new ClassDefineException(fieldDescriptor() + " <items> values must match field length");
            }
        }
    }

    private String fieldDescriptor() {
        return "Field " + getName() + "@" + getOffset() + "+" + getLength();
    }
}
