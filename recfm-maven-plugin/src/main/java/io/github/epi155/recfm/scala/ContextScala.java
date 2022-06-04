package io.github.epi155.recfm.scala;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.exec.LanguageContext;
import io.github.epi155.recfm.lang.AccessField;
import io.github.epi155.recfm.lang.InitializeField;
import io.github.epi155.recfm.lang.ValidateField;
import io.github.epi155.recfm.type.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntFunction;

@Slf4j
public class ContextScala extends LanguageContext implements IndentAble {
    static void writeCopyright(PrintWriter pw) {
        String now = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        pw.println("/*");
        pw.printf(" * Generated by recfm-maven-plugin %s at %s%n", VERSION, now);
        pw.println(" */");
    }

    @Override
    protected void generateClass(ClassDefine define, String cwd, String wrtPackage, GenerateArgs ga, Defaults defaults) {
        log.info("- Prepare class {} ...", define.getName());
        val classFile = new File(cwd + File.separator + define.getName() + ".scala");

        // no shorthand -> full check !?
        if (define.noBadName() && define.noDuplicateName() && define.noHole() && define.noOverlap()) {
            try (PrintWriter pw = new PrintWriter(classFile)) {
                writePackage(pw, wrtPackage);
                if (!wrtPackage.equals(ga.utilPackage))
                    writeImport(pw, ga.utilPackage);
                generateClassCode(pw, define, ga, defaults, n -> String.format("%d", n - 1));
                log.info("  * Created.");
            } catch (IOException e) {
                throw new ClassDefineException(e);
            }
        } else {
            throw new ClassDefineException("Class <" + define.getName() + "> bad defined");
        }
    }

    @Override
    protected AccessField accessField(PrintWriter pw, IntFunction<String> pos) {
        return new AccessFieldScala(pw, pos);
    }

    @Override
    protected InitializeField initializeField(PrintWriter pw, ClassDefine struct, Defaults defaults) {
        return new InitializeFieldScala(pw, struct, defaults);
    }

    @Override
    protected ValidateField validateField(PrintWriter pw, String name, Defaults defaults) {
        return new ValidateFieldScala(pw, name, defaults);
    }

    @Override
    public void copyTemplate(ClassLoader classLoader, String utilDirectory, String utilPackage) {
        Templates.copy(classLoader, utilDirectory, utilPackage);
    }

    private void writePackage(PrintWriter pw, String packg) {
        writeCopyright(pw);
        pw.printf("package %s%n%n", packg);
    }

    private void writeImport(PrintWriter pw, String packg) {
        pw.printf("import %s.{FieldValidateHandler, FixRecord, OverflowAction, UnderflowAction}%n", packg);
        pw.println();
    }

    private void generateClassCode(PrintWriter pw, ClassDefine struct, GenerateArgs ga, Defaults defaults, IntFunction<String> pos) {
        writeBeginObject(pw, struct);
        writeConstant(pw, struct, 0);
        writeFactories(pw, struct);
        writeEndClass(pw, 0);

        writeBeginClass(pw, struct);
        struct.getFields().forEach(it -> {
            if (it instanceof SelfCheck) ((SelfCheck) it).selfCheck();
            if (it instanceof ParentFields) generateGroupCode((ParentFields) it, pw, 2, ga, defaults, pos);
        });
        val access = accessField(pw, pos);
        writeCtorVoid(pw, struct.getName());
        writeCtorParm(pw, struct);
        writeInitializer(pw, struct, defaults);
        writeValidator(pw, struct, defaults);
        struct.getFields().forEach(it -> {
            if (it instanceof SettableField) access.createMethods((SettableField) it, 0, ga);
        });
        writeEndClass(pw, 0);
    }

    //    def of(s: String) = new SuezHead(s)
//    def of(r: FixRecord) = new SuezHead(r)
    private void writeFactories(PrintWriter pw, ClassDefine struct) {
        pw.printf("  def of(s: String) = new %s(s)%n", struct.getName());
        pw.printf("  def of(r: FixRecord) = new %s(r)%n", struct.getName());
    }

    private void generateGroupCode(ParentFields fld, PrintWriter pw, int indent, GenerateArgs ga, Defaults defaults, IntFunction<String> pos) {
        AccessField access;
        if (fld instanceof FieldOccurs) {
            writeBeginClassOccurs(pw, (FieldOccurs) fld, indent);
            access = accessField(pw, n -> String.format("%d+shift", n - 1));
        } else if (fld instanceof FieldGroup) {
            writeBeginClassGroup(pw, fld.getName(), indent);
            access = accessField(pw, pos);
        } else {
            throw new RuntimeException();
        }
        fld.getFields().forEach(it -> {
            if (it instanceof SelfCheck) ((SelfCheck) it).selfCheck();
            if (it instanceof ParentFields) generateGroupCode((ParentFields) it, pw, indent + 2, ga, defaults, pos);
        });
        fld.getFields().forEach(it -> {
            if (it instanceof SettableField) access.createMethods((SettableField) it, indent, ga);
        });
        writeEndClass(pw, indent);
        if (fld instanceof FieldOccurs) {
            writeClassOccurs(pw, (FieldOccurs) fld, indent);
        } else {
            writeClassGroup(pw, fld.getName(), indent);
        }
    }

    private void writeClassGroup(PrintWriter pw, String name, int indent) {
        String capName = capitalize(name);
        indent(pw, indent);
        pw.printf("val %s = new this.%s%n", name, capName);
    }

    private void writeBeginClassGroup(PrintWriter pw, String name, int indent) {
        String capName = capitalize(name);
        indent(pw, indent);
        pw.printf("class %s {%n", capName);
    }

    private void writeBeginClassOccurs(PrintWriter pw, FieldOccurs fld, int indent) {
        String capName = capitalize(fld.getName());
        indent(pw, indent);
        pw.printf("class %s(private val shift: Int) {%n", capName);
    }

    private void writeClassOccurs(PrintWriter pw, FieldOccurs fld, int indent) {
        String capName = capitalize(fld.getName());
        indent(pw, indent);
        pw.printf("val %s = Array[%s](%n", fld.getName(), capName);
        val times = fld.getTimes();
        for (int k = 0, j = times, shift = 0; k < times; k++, j--, shift += fld.getLength()) {
            indent(pw, indent);
            pw.printf("  new this.%s(%d)%s%n", capName, shift, (j > 1 ? "," : ")"));
        }
        pw.println();
    }

    private void writeValidator(PrintWriter pw, ClassDefine struct, Defaults defaults) {
        int padWidth = struct.evalPadWidth(6);
        val validator = validateField(pw, struct.getName(), defaults);
        pw.printf("  override protected def validateFields(handler: FieldValidateHandler): Boolean =%n");
        AtomicBoolean firstCheck = new AtomicBoolean(true);
        for (NakedField fld : struct.getFields()) {
            validator.validate(fld, padWidth, 1, firstCheck);
        }
        if (firstCheck.get()) {
            pw.printf("        false%n");
        }
        pw.printf("  override protected def auditFields(handler: FieldValidateHandler): Boolean =%n");
        pw.printf("    var error = false%n");
        AtomicBoolean firstAudit = new AtomicBoolean(true);
        for (NakedField fld : struct.getFields()) {
            if (fld instanceof CheckAware && ((CheckAware) fld).isAudit()) {
                validator.validate(fld, padWidth, 1, firstAudit);
            }
        }
        if (firstAudit.get()) {
            pw.printf("        false%n");
        }
    }

    private void writeCtorVoid(PrintWriter pw, String name) {
        pw.printf("  def this() = this(%s.LRECL, null, null, false, false)%n", name);
    }

    private void writeCtorParm(PrintWriter pw, ClassDefine define) {
        define.onOverflowDefault(LoadOverflowAction.Trunc);
        define.onUnderflowDefault(LoadUnderflowAction.Pad);
        pw.printf("  private def this(s: String) = this(%s.LRECL, s, null, %b, %b)%n",
            define.getName(), define.onOverflowThrowError(), define.onUnderflowThrowError());
        pw.printf("  private def this(r: FixRecord) = this(%s.LRECL, null, r, %b, %b)%n",
            define.getName(), define.onOverflowThrowError(), define.onUnderflowThrowError());
    }

    private void writeInitializer(PrintWriter pw, ClassDefine struct, Defaults defaults) {
        pw.printf("  override protected def initialize(): Unit = {%n");
        val initializer = initializeField(pw, struct, defaults);
        struct.getFields().forEach(it -> initializer.field(it, 1));
        closeBrace(pw);
    }

    private void closeBrace(PrintWriter pw) {
        pw.printf("  }%n%n");
    }

    private void writeEndClass(PrintWriter pw, int indent) {
        indent(pw, indent);
        pw.write("}");
        pw.println();
    }

    private void writeConstant(PrintWriter pw, ParentFields struct, int deep) {
        if (deep == 0)
            pw.printf("  val LRECL = %d%n", struct.getLength());
        struct.getFields().forEach(it -> {
            if (it instanceof HaveConstants)
                ((HaveConstants) it).writeConstant((u, v) -> pw.printf("  private val %s = %s%n", u, v));
            if (it instanceof ParentFields) writeConstant(pw, (ParentFields) it, deep + 1);
        });
    }

    private void writeBeginObject(PrintWriter pw, ClassDefine struct) {
        pw.printf("object %s {%n", struct.getName());
    }

    private void writeBeginClass(PrintWriter pw, ClassDefine struct) {
        pw.printf("class %s private (length: Int, s: String, r: FixRecord, overflowError: Boolean, underflowError: Boolean)\n" +
            "  extends FixRecord(length, s, r, overflowError, underflowError) {%n", struct.getName());
    }

}
