package io.github.epi155.recfm.exec;

import io.github.epi155.recfm.type.ClassDefineException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.StringTokenizer;

@Slf4j
public class Tools {
    private Tools() {
    }

    public static String makeDirectory(@NotNull String baseDir, @Nullable String packg) {
        if (packg == null) return null;
        File base = new File(baseDir);
        if (!base.exists()) {
            log.warn("Base Direcory <{}}> does not exist, creating", baseDir);
            if (!base.mkdirs())
                throw new ClassDefineException("Error creating Base Direcory <" + baseDir + ">");
        }
        if (!base.isDirectory()) throw new ClassDefineException("Base Direcory <" + baseDir + "> is not a Direcory");
        StringTokenizer st = new StringTokenizer(packg, ".");
        String cwd = baseDir;
        while (st.hasMoreElements()) {
            val d = st.nextElement();
            val tmp = cwd + File.separator + d;
            mkdir(tmp);
            cwd = tmp;
        }
        return cwd;
    }

    private static void mkdir(String tmp) {
        val f = new File(tmp);
        if ((!f.exists()) && (!f.mkdir()))
            throw new ClassDefineException("Cannot create direcory <" + tmp + ">");
    }
}
