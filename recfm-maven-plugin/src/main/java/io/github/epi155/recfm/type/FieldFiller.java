package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FieldFiller extends NakedField {
    private Character fillChar;
    private CheckChar check;
}
