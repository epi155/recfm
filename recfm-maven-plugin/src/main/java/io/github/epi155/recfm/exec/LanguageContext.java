package io.github.epi155.recfm.exec;

import io.github.epi155.recfm.lang.AccessField;
import io.github.epi155.recfm.lang.InitializeField;
import io.github.epi155.recfm.lang.ValidateField;
import io.github.epi155.recfm.type.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

@Slf4j
public abstract class LanguageContext {
//    protected static final String VERSION = Optional.ofNullable(LanguageContext.class.getPackage().getImplementationVersion()).orElse("N/A");
    protected static final String R_GROUP;
    protected static final String R_NAME;
    protected static final String R_VERSION;

    static {
        java.util.Properties p = new Properties();
        String group;
        String name;
        String version;
        try (java.io.InputStream is = LanguageContext.class.getClassLoader().getResourceAsStream(("recfm.properties"))) {
            p.load(is);
            group = p.getProperty("project.group");
            name = p.getProperty("project.name");
            version = p.getProperty("project.version");
        } catch (IOException e) {
            throw new RuntimeException();
//            group = "no-group";
//            name = "no-name";
//            version = "no-version";
        }
        R_GROUP = group;
        R_NAME = name;
        R_VERSION = version;
    }
    private static final String CONSTANT = "<Constant>";

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

    protected abstract DumpFactory dumpFactory();

    protected List<DumpAware> prepareDump(List<NakedField> fields) {
        List<DumpPicure> lst = new ArrayList<>();
        dumpFields(lst, "", fields, 0);

        return lst.stream().map(DumpPicure::normalize).collect(Collectors.toList());
    }

    private Collection<? extends DumpPicure> occursDump(String prefix, int times, int size, List<NakedField> fields, int initBias) {
        List<DumpPicure> lst = new ArrayList<>();

        for (int k = 1, bias = initBias; k <= times; k++, bias += size) {
            String px = prefix + "[" + k + "].";
            dumpFields(lst, px, fields, bias);
        }
        return lst;
    }

    private Collection<? extends DumpPicure> groupDump(String prefix, List<NakedField> fields, int bias) {
        List<DumpPicure> lst = new ArrayList<>();

        String px = prefix + ".";
        dumpFields(lst, px, fields, bias);
        return lst;
    }

    private void dumpFields(List<DumpPicure> lst, String px, List<NakedField> fields, int bias) {
        val dump = dumpFactory();
        for (NakedField field : fields) {
            if (field instanceof FieldConstant) {
                lst.add(dump.newPicture(CONSTANT, bias + field.getOffset(), field.getLength(), "X"));
            } else if (field instanceof NamedField) {
                NamedField na = (NamedField) field;
                if (na.isRedefines()) continue;
                if (na instanceof SettableField) {
                    SettableField fs = (SettableField) na;
                    lst.add(dump.newPicture(px + fs.getName(), bias + fs.getOffset(), fs.getLength(), fs.picture()));
                } else if (na instanceof FieldOccurs) {
                    FieldOccurs fo = (FieldOccurs) na;
                    lst.addAll(occursDump(px + fo.getName(), fo.getTimes(), fo.getLength(), fo.getFields(), bias));
                } else if (na instanceof FieldGroup) {
                    FieldGroup fg = (FieldGroup) na;
                    lst.addAll(groupDump(px + fg.getName(), fg.getFields(), bias));
                }
            }
        }
    }

    protected abstract void generateClass(ClassDefine define, String cwd, String packageName, GenerateArgs ga, Defaults defaults);

    protected abstract AccessField accessField(PrintWriter pw, IntFunction<String> pos, String name);

    protected abstract InitializeField initializeField(PrintWriter pw, ClassDefine struct, Defaults defaults);

    protected abstract ValidateField validateField(PrintWriter pw, String name, Defaults defaults);

    public abstract void copyTemplate(ClassLoader classLoader, @NotNull String utilDirectory, @NotNull String utilPackage);

}
