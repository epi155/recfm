package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.exec.LanguageContext;
import io.github.epi155.recfm.lang.AccessField;
import io.github.epi155.recfm.lang.ActionField;
import io.github.epi155.recfm.type.FieldAbc;
import io.github.epi155.recfm.type.FieldCustom;
import io.github.epi155.recfm.type.FieldNum;
import io.github.epi155.recfm.type.IndentAble;
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
public class AccessFieldScala extends AccessField implements IndentAble {
    private final ActionField<FieldAbc> delegateAbc;
    private final ActionField<FieldNum> delegateNum;
    private final ActionField<FieldCustom> delegateUse;

    /**
     * Constructor
     *
     * @param pw  output writer
     * @param pos field offset to string form
     */
    public AccessFieldScala(PrintWriter pw, IntFunction<String> pos) {
        delegateAbc = new ScalaFieldAbc(pw, pos);
        delegateNum = new ScalaFieldNum(pw, pos);
        delegateUse = new ScalaFieldCustom(pw, pos);
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
        delegateUse.access(fld, wrkName, indent, ga);
    }

}
