package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class FieldGroup extends NamedField implements ParentFields {
    private List<NakedField> fields = new ArrayList<>();

    @Override
    public boolean noHole() {
        return noHole(getOffset());
    }

    @Override
    public boolean noOverlap() {
        return noOverlap(getOffset());
    }

}
