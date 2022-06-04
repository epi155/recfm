package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class ClassesDefine {
    private String packageName;
    private Defaults defaults = new Defaults();
    private List<ClassDefine> classes = new ArrayList<>();
}
