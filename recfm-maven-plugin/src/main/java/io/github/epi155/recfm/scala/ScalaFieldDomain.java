package io.github.epi155.recfm.scala;

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

public class ScalaFieldDomain extends ActionField<FieldDomain> implements ScalaFieldTools {
    private final String name;

    public ScalaFieldDomain(PrintWriter pw) {
        // prepare
        super(pw);
        this.name = null;
    }
    public ScalaFieldDomain(PrintWriter pw, IntFunction<String> pos, String name) {
        // access
        super(pw, pos);
        this.name = name;
    }

    public ScalaFieldDomain(PrintWriter pw, String name) {
        super(pw);
        this.name = name;
    }

    @Override
    public void access(FieldDomain fld, String wrkName, int indent, GenerateArgs ga) {
        pushIndent(indent);
        printf("  final def %s: String = {%n", fld.getName());
        if (ga.getCheck) chkGetter(fld);
        printf("    abc(%s, %d)%n", pos.apply(fld.getOffset()), fld.getLength());
        printf("  }%n");
        printf("  final def %s_=(s: String): Unit = {%n", fld.getName());
        if (ga.setCheck) chkSetter(fld);
        printf("    abc(s, %s, %d)%n",
            pos.apply(fld.getOffset()), fld.getLength());
        printf("  }%n");
        popIndent();
    }

    private void chkGetter(@NotNull FieldDomain fld) {
        printf("    testArray(%1$s, %2$d, %3$s.DOMAIN_AT%4$sPLUS%2$d)%n",
            pos.apply(fld.getOffset()), fld.getLength(),
            name, pos.apply(fld.getOffset()+1));
    }

    private void chkSetter(@NotNull FieldDomain fld) {
        printf("    testArray(s, %s.DOMAIN_AT%sPLUS%d)%n",
            name, pos.apply(fld.getOffset()+1), fld.getLength());
    }

    @Override
    public void prepare(@NotNull FieldDomain fld, int bias) {
        val items = fld.getItems();
        printf("  private val VALUE_AT%dPLUS%d = \"%s\";%n",
            fld.getOffset(), fld.getLength(), StringEscapeUtils.escapeJava(items[0]));
        val works = Arrays.asList(items);
        val domain = works.stream()
            .sorted()
            .map(it -> "\"" + StringEscapeUtils.escapeJava(it) + "\"")
            .collect(Collectors.joining(","));
        printf("  private val DOMAIN_AT%dPLUS%d = Array( %s )%n", fld.getOffset(), fld.getLength(), domain);
    }
    @Override
    public void initialize(@NotNull FieldDomain fld, int bias) {
        printf("    fill(%5d, %4d, %s.VALUE_AT%dPLUS%d);%n",
            fld.getOffset() - bias, fld.getLength(), name, fld.getOffset(), fld.getLength());
    }

    @Override
    public void validate(@NotNull FieldDomain fld, int w, int bias, boolean isFirst) {
        String prefix = prefixOf(isFirst);
        printf("%s checkArray(\"%s\"%s, %5d, %4d, handler, %s.DOMAIN_AT%dPLUS%d)%n", prefix,
            fld.getName(), fld.pad(w),
            fld.getOffset() - bias, fld.getLength(),
            name, fld.getOffset(), fld.getLength()
        );

    }
}
