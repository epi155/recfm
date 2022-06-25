package io.github.epi155.recfm.exec;


import io.github.epi155.recfm.type.*;
import lombok.val;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Mojo(name = "fixed", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class RecordFormatMojo extends AbstractMojo {

    private static final String SET_LENGTH = "setLength";
    private static final String GET_LENGTH = "getLength";

    private static final String SET_OFFSET = "setOffset";
    private static final String GET_OFFSET = "getOffset";

    private static final String SET_REDEFINES = "setRedefines";
    private static final String GET_REDEFINES = "getRedefines";

    @Parameter(defaultValue = "${project.build.sourceDirectory}", required = true)
    private String outputSourceDirectory;

    @Parameter
    private String outputUtilPackage;

    @Parameter(defaultValue = "${project.build.resources[0].directory}", required = true)
    private String settingsDirectory;

    @Parameter(defaultValue = "java", required = true)
    private LanguageEnum language;

    @Parameter(defaultValue = "4", required = true)
    private int align;

    @Parameter(defaultValue = "false", required = true)
    private boolean doc;
    @Parameter(defaultValue = "true", required = true)
    private boolean enforceGetter;
    @Parameter(defaultValue = "true", required = true)
    private boolean enforceSetter;

    @Parameter(required = true)
    private String[] settings;

    public static Yaml prepareYaml() {
        val constructor = new Constructor(ClassesDefine.class);
        Representer representer = new Representer();

        tuningClassDef(constructor, representer);
        tuningField(constructor, representer, "!Abc", FieldAbc.class);
        tuningField(constructor, representer, "!Num", FieldNum.class);
        tuningField(constructor, representer, "!Use", FieldCustom.class);
        tuningField(constructor, representer, "!Fil", FieldFiller.class);
        tuningField(constructor, representer, "!Val", FieldConstant.class);
        tuningField(constructor, representer, "!Grp", FieldGroup.class);
        tuningField(constructor, representer, "!Occ", FieldOccurs.class);

        return new Yaml(constructor, representer);
    }

    private static void tuningField(Constructor c, Representer r, String tag, Class<? extends NakedField> f) {
        TypeDescription td = new TypeDescription(f, tag);
        td.substituteProperty("at", int.class, GET_OFFSET, SET_OFFSET);
        td.substituteProperty("len", int.class, GET_LENGTH, SET_LENGTH);
        if (NamedField.class.isAssignableFrom(f)) {
            td.substituteProperty("red", boolean.class, GET_REDEFINES, SET_REDEFINES);
        }
        if (f == FieldOccurs.class)
            td.substituteProperty("x", int.class, "getTimes", "setTimes");
        else if (f == FieldNum.class)
            td.substituteProperty("num", boolean.class, "getNumericAccess", "setNumericAccess");
        else if (f == FieldConstant.class)
            td.substituteProperty("val", String.class, "getValue", "setValue");
        else if (f == FieldCustom.class) {
            td.substituteProperty("init", char.class, "getInitChar", "setInitChar");
            td.substituteProperty("pad", char.class, "getPadChar", "setPadChar");
        }
        c.addTypeDescription(td);
        r.addTypeDescription(td);
    }

    private static void tuningClassDef(Constructor constructor, Representer representer) {
        TypeDescription td = new TypeDescription(ClassDefine.class);
        td.substituteProperty("len", int.class, GET_LENGTH, SET_LENGTH);
        constructor.addTypeDescription(td);
        representer.addTypeDescription(td);
    }

    public void execute() throws MojoExecutionException {
        getLog().info("Check for output directory ...");
        String outputUtilDirectory = Tools.makeDirectory(outputSourceDirectory, outputUtilPackage);

        Yaml yaml = prepareYaml();

        val args = GenerateArgs.builder()
            .sourceDirectory(outputSourceDirectory)
            .utilPackage(outputUtilPackage)
            .align(align)
            .doc(doc)
            .setCheck(enforceSetter)
            .getCheck(enforceGetter)
            .build();

        getLog().info("Settings directory: " + settingsDirectory);
        for (String setting : settings) {
            getLog().info("Generate from " + setting);
            try (InputStream inputStream = new FileInputStream(settingsDirectory + File.separator + setting)) {
                ClassesDefine structs = yaml.load(inputStream);
                language.generate(structs, args);
            } catch (FileNotFoundException e) {
                getLog().warn("Setting " + setting + " does not exist, ignored.");
            } catch (Exception e) {
                getLog().error(e.toString());
                throw new MojoExecutionException("Failed to execute plugin", e);
            }
        }

        if (outputUtilDirectory != null && outputUtilPackage != null) {
            getLog().info("Coping templates ...");
            language.copyTemplate(this.getClass().getClassLoader(), outputUtilDirectory, outputUtilPackage);
        }

        getLog().info("Done.");
    }

}
