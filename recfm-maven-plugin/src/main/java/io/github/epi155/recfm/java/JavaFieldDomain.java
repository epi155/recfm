package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.FieldDomain;
import lombok.val;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class JavaFieldDomain extends ActionField<FieldDomain> implements JavaFieldTools {
    public JavaFieldDomain(PrintWriter pw) {
        super(pw);
    }
    public JavaFieldDomain(PrintWriter pw, IntFunction<String> pos) {
        super(pw, pos);
    }

    @Override
    public void access(FieldDomain fld, String wrkName, int indent, @NotNull GenerateArgs ga) {
        pushIndent(indent);
        if (ga.doc) docGetter(fld);
        printf("    public String get%s() {%n", wrkName);
        if (ga.getCheck) chkGetter(fld);
        printf("        return getAbc(%s, %d);%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("    }%n");
        if (ga.doc) docSetter(fld);
        printf("    public void set%s(String s) {%n", wrkName);
        if (ga.setCheck) chkSetter(fld);
        printf("        setAbc(s, %s, %d);%n",
            pos.apply(fld.getOffset()), fld.getLength());
        printf("    }%n");
    }

    private void chkSetter(@NotNull FieldDomain fld) {
        printf("        testArray(s, DOMAIN_AT%sPLUS%d);%n", pos.apply(fld.getOffset()+1), fld.getLength());
    }

    private void chkGetter(@NotNull FieldDomain fld) {
        printf("        testArray(%1$s, %2$d, DOMAIN_AT%3$sPLUS%2$d);%n", pos.apply(fld.getOffset()), fld.getLength(), pos.apply(fld.getOffset()+1));
    }

    @Override
    public void initialize(@NotNull FieldDomain fld, int bias) {
        printf("        fill(%5d, %4d, VALUE_AT%dPLUS%d);%n",
            fld.getOffset() - bias, fld.getLength(), fld.getOffset(), fld.getLength());
    }

    @Override
    public void validate(@NotNull FieldDomain fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        printf("%s checkArray(\"%s\"%s, %5d, %4d, handler, DOMAIN_AT%dPLUS%d);%n", prefix,
            fld.getName(), fld.pad(w),
            fld.getOffset() - bias, fld.getLength(),
            fld.getOffset(), fld.getLength()
        );
    }

    @Override
    public void prepare(@NotNull FieldDomain fld, int bias) {
        val items = fld.getItems();
        printf("    private static final String VALUE_AT%dPLUS%d = \"%s\";%n",
            fld.getOffset(), fld.getLength(), StringEscapeUtils.escapeJava(items[0]));
        val works = Arrays.asList(items);
        val domain = works.stream()
            .sorted()
            .map(it -> "\"" + StringEscapeUtils.escapeJava(it) + "\"")
            .collect(Collectors.joining(","));
        printf("    private static final String[] DOMAIN_AT%dPLUS%d = { %s };%n", fld.getOffset(), fld.getLength(), domain);
    }
}
