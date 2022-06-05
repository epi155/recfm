package io.github.epi155.recfm.exec;

import io.github.epi155.recfm.lang.AccessField;
import io.github.epi155.recfm.lang.InitializeField;
import io.github.epi155.recfm.lang.ValidateField;
import io.github.epi155.recfm.type.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.PrintWriter;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

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

    protected abstract DumpFactory dumpFactory();

    protected List<DumpAware> prepareDump(List<NakedField> fields) {
        val dump = dumpFactory();
        List<DumpPicure> lst = new ArrayList<>();
        for (NakedField field : fields) {
            if (field instanceof FieldAbc) {
                FieldAbc fa = (FieldAbc) field;
                if (fa.isRedefines()) continue;
                lst.add(dump.newPicture(fa.getName(), fa.getOffset(), fa.getLength(), "X"));
            } else if (field instanceof FieldNum) {
                FieldNum fn = (FieldNum) field;
                if (fn.isRedefines()) continue;
                lst.add(dump.newPicture(fn.getName(), fn.getOffset(), fn.getLength(), "9"));
            } else if (field instanceof FieldConstant) {
                lst.add(dump.newPicture("<Constant>", field.getOffset(), field.getLength(), "X"));
            } else if (field instanceof FieldOccurs) {
                FieldOccurs fo = (FieldOccurs) field;
                if (fo.isRedefines()) continue;
                lst.addAll(occursDump(fo.getName(), fo.getTimes(), fo.getLength(), fo.getFields(), 0));
            } else if (field instanceof FieldGroup) {
                FieldGroup fg = (FieldGroup) field;
                if (fg.isRedefines()) continue;
                lst.addAll(groupDump(fg.getName(), fg.getFields(), 0));
            }
        }
        return lst.stream().map(DumpPicure::normalize).collect(Collectors.toList());
    }

    private Collection<? extends DumpPicure> occursDump(String prefix, int times, int size, List<NakedField> fields, int initBias) {
        val dump = dumpFactory();
        List<DumpPicure> lst = new ArrayList<>();

        for (int k = 1, bias = initBias; k <= times; k++, bias += size) {
            String px = prefix + "[" + k + "].";
            for (NakedField field : fields) {
                if (field instanceof FieldAbc) {
                    FieldAbc fa = (FieldAbc) field;
                    if (fa.isRedefines()) continue;
                    lst.add(dump.newPicture(px + fa.getName(), bias + fa.getOffset(), fa.getLength(), "X"));
                } else if (field instanceof FieldNum) {
                    FieldNum fn = (FieldNum) field;
                    if (fn.isRedefines()) continue;
                    lst.add(dump.newPicture(px + fn.getName(), bias + fn.getOffset(), fn.getLength(), "9"));
                } else if (field instanceof FieldConstant) {
                    lst.add(dump.newPicture("<Constant>", bias + field.getOffset(), field.getLength(), "X"));
                } else if (field instanceof FieldOccurs) {
                    FieldOccurs fo = (FieldOccurs) field;
                    if (fo.isRedefines()) continue;
                    lst.addAll(occursDump(px + fo.getName(), fo.getTimes(), fo.getLength(), fo.getFields(), bias));
                } else if (field instanceof FieldGroup) {
                    FieldGroup fg = (FieldGroup) field;
                    if (fg.isRedefines()) continue;
                    lst.addAll(groupDump(px + fg.getName(), fg.getFields(), bias));
                }
            }
        }
        return lst;
    }

    private Collection<? extends DumpPicure> groupDump(String prefix, List<NakedField> fields, int bias) {
        val dump = dumpFactory();
        List<DumpPicure> lst = new ArrayList<>();

        for (NakedField field : fields) {
            if (field instanceof FieldAbc) {
                FieldAbc fa = (FieldAbc) field;
                if (fa.isRedefines()) continue;
                lst.add(dump.newPicture(prefix + "." + fa.getName(), bias + fa.getOffset(), fa.getLength(), "X"));
            } else if (field instanceof FieldNum) {
                FieldNum fn = (FieldNum) field;
                if (fn.isRedefines()) continue;
                lst.add(dump.newPicture(prefix + "." + fn.getName(), bias + fn.getOffset(), fn.getLength(), "9"));
            } else if (field instanceof FieldConstant) {
                lst.add(dump.newPicture("<Constant>", bias + field.getOffset(), field.getLength(), "X"));
            } else if (field instanceof FieldOccurs) {
                FieldOccurs fo = (FieldOccurs) field;
                if (fo.isRedefines()) continue;
                lst.addAll(occursDump(prefix + "." + fo.getName(), fo.getTimes(), fo.getLength(), fo.getFields(), bias));
            } else if (field instanceof FieldGroup) {
                FieldGroup fg = (FieldGroup) field;
                if (fg.isRedefines()) continue;
                lst.addAll(groupDump(prefix + "." + fg.getName(), fg.getFields(), bias));
            }
        }
        return lst;
    }

    protected abstract void generateClass(ClassDefine define, String cwd, String packageName, GenerateArgs ga, Defaults defaults);

    protected abstract AccessField accessField(PrintWriter pw, IntFunction<String> pos);

    protected abstract InitializeField initializeField(PrintWriter pw, ClassDefine struct, Defaults defaults);

    protected abstract ValidateField validateField(PrintWriter pw, String name, Defaults defaults);

    public abstract void copyTemplate(ClassLoader classLoader, String utilDirectory, String utilPackage);

}
