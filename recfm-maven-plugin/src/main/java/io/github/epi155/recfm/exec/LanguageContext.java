package io.github.epi155.recfm.exec;

import io.github.epi155.recfm.lang.AccessField;
import io.github.epi155.recfm.lang.InitializeField;
import io.github.epi155.recfm.lang.ValidateField;
import io.github.epi155.recfm.type.ClassDefine;
import io.github.epi155.recfm.type.ClassDefineException;
import io.github.epi155.recfm.type.ClassesDefine;
import io.github.epi155.recfm.type.Defaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.PrintWriter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;

@Slf4j
public abstract class LanguageContext {
    protected static final String VERSION = Optional.ofNullable(LanguageContext.class.getPackage().getImplementationVersion()).orElse("N/A");

    public static String getWrkName(String name) {
        val fst = String.valueOf(Character.toUpperCase(name.charAt(0)));
        return (name.length() > 1) ? (fst + name.substring(1)) : fst;
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void generate(ClassesDefine structs, GenerateArgs args) {
        String cwd = Tools.makeDirectory(args.sourceDirectory, structs.getPackageName());
        structs.getClasses().
            forEach(it -> generateClass(it, cwd, structs.getPackageName(), args, structs.getDefaults()));
    }

    protected void checkForVoid(ClassDefine struct) {
        // no shorthand -> full check !?
        long voidFields = struct.getFields().stream().filter(Objects::isNull).count();
        if (voidFields > 0) {
            log.error("{} void field definitions", voidFields);
            throw new ClassDefineException("Class <" + struct.getName() + "> bad defined");
        }
    }

    protected abstract void generateClass(ClassDefine define, String cwd, String packageName, GenerateArgs ga, Defaults defaults);

    protected abstract AccessField accessField(PrintWriter pw, IntFunction<String> pos);

    protected abstract InitializeField initializeField(PrintWriter pw, ClassDefine struct, Defaults defaults);

    protected abstract ValidateField validateField(PrintWriter pw, String name, Defaults defaults);

    public abstract void copyTemplate(ClassLoader classLoader, String utilDirectory, String utilPackage);

}
