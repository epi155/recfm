package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class ClassDefine implements ParentFields {
    private String name;
    private int length;
    private LoadOverflowAction onOverflow;
    private LoadUnderflowAction onUnderflow;
    private List<NakedField> fields = new ArrayList<>();

    public void onOverflowDefault(LoadOverflowAction action) {
        if (onOverflow == null) onOverflow = action;
    }

    public void onUnderflowDefault(LoadUnderflowAction action) {
        if (onUnderflow == null) onUnderflow = action;
    }

    public boolean onOverflowThrowError() {
        return onOverflow == LoadOverflowAction.Error;
    }

    public boolean onUnderflowThrowError() {
        return onUnderflow == LoadUnderflowAction.Error;
    }

}
