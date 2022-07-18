package io.github.epi155.recfm.java;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.exec.LanguageContext;
import io.github.epi155.recfm.lang.AccessField;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.FieldAbc;
import io.github.epi155.recfm.type.FieldCustom;
import io.github.epi155.recfm.type.FieldDomain;
import io.github.epi155.recfm.type.FieldNum;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.function.IntFunction;

/**
 * class that generates the java methods for accessing the fields.
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class AccessFieldJava extends AccessField /*implements IndentAble*/ {
    private final ActionField<FieldAbc> delegateAbc;
    private final ActionField<FieldNum> delegateNum;
    private final ActionField<FieldCustom> delegateCus;
    private final ActionField<FieldDomain> delegateDom;

    /**
     * Contructor
     *
     * @param pw  print writer
     * @param pos offset field to string form
     */
    public AccessFieldJava(PrintWriter pw, IntFunction<String> pos) {
        this.delegateAbc = new JavaFieldAbc(pw, pos);
        this.delegateNum = new JavaFieldNum(pw, pos);
        this.delegateCus = new JavaFieldCustom(pw, pos);
        this.delegateDom = new JavaFieldDomain(pw, pos);
    }

    @Override
    protected void createMethodsDomain(FieldDomain fld, int indent, GenerateArgs ga) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
        delegateDom.access(fld, wrkName, indent, ga);
    }

    protected void createMethodsNum(@NotNull FieldNum fld, int indent, @NotNull GenerateArgs ga) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
        delegateNum.access(fld, wrkName, indent, ga);
    }

    @Override
    protected void createMethodsAbc(@NotNull FieldAbc fld, int indent, GenerateArgs ga) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
        delegateAbc.access(fld, wrkName, indent, ga);
    }

    @Override
    protected void createMethodsCustom(FieldCustom fld, int indent, GenerateArgs ga) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
        delegateCus.access(fld, wrkName, indent, ga);
    }
}
