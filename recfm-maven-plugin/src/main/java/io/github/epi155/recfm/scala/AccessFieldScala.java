package io.github.epi155.recfm.scala;

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
 * class that generates methods for accessing the fields in scala language.
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class AccessFieldScala extends AccessField /*implements IndentAble*/ {
    private final ActionField<FieldAbc> delegateAbc;
    private final ActionField<FieldNum> delegateNum;
    private final ActionField<FieldCustom> delegateCus;
    private final ActionField<FieldDomain> delegateDom;

    /**
     * Constructor
     *
     * @param pw  output writer
     * @param pos field offset to string form
     */
    public AccessFieldScala(PrintWriter pw, IntFunction<String> pos, String name) {
        delegateAbc = new ScalaFieldAbc(pw, pos);
        delegateNum = new ScalaFieldNum(pw, pos);
        delegateCus = new ScalaFieldCustom(pw, pos, name);
        delegateDom = new ScalaFieldDomain(pw, pos, name);
    }

    @Override
    protected void createMethodsDomain(FieldDomain fld, int indent, GenerateArgs ga) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
        delegateDom.access(fld, wrkName, indent, ga);
    }

    @Override
    protected void createMethodsNum(@NotNull FieldNum fld, int indent, GenerateArgs ga) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
        delegateNum.access(fld, wrkName, indent, ga);
    }

    @Override
    protected void createMethodsAbc(FieldAbc fld, int indent, GenerateArgs ga) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
        delegateAbc.access(fld, wrkName, indent, ga);
    }

    @Override
    protected void createMethodsCustom(FieldCustom fld, int indent, GenerateArgs ga) {
        val wrkName = LanguageContext.getWrkName(fld.getName());
        delegateCus.access(fld, wrkName, indent, ga);
    }

}
