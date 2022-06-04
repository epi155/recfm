package io.github.epi155.recfm.java;

import io.github.epi155.recfm.type.ClassDefineException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Objects;

@Slf4j
public class Templates {
    private static final String[] models = {
        "FieldValidateHandler",
        "FixBasic",
        "FixEngine",
        "FixError",
        "FixRecord",
        "OverflowAction",
        "UnderflowAction",
        "ValidateError"
    };

    public static void copy(ClassLoader cl, String cwd, @Nullable String packg) {
        if (packg == null) return;
        for (String model : models) {
            String source = "models/java/" + model + ".java";
            File target = new File(cwd + File.separator + model + ".java");
            log.debug("Coping {} ...", model);
            copyModel(cl, source, target, packg);
        }
    }

    private static void copyModel(ClassLoader cl, String source, File target, @NotNull String packg) {
        try (
            PrintWriter pw = new PrintWriter(target);
            InputStream is = cl.getResourceAsStream(source);
            InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(is));
            BufferedReader br = new BufferedReader(isr)

        ) {
            writePackage(pw, packg);
            String line = br.readLine();
            while (line != null) {
                pw.println(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            throw new ClassDefineException(e);
        }
    }

    private static void writePackage(PrintWriter pw, String packg) {
        ContextJava.writeCopyright(pw);
        pw.printf("package %s;%n%n", packg);
    }
}
