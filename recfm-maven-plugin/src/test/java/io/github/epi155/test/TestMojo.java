package io.github.epi155.test;

import io.github.epi155.recfm.exec.GenerateArgs;
import io.github.epi155.recfm.exec.LanguageEnum;
import io.github.epi155.recfm.exec.RecordFormatMojo;
import io.github.epi155.recfm.exec.Tools;
import io.github.epi155.recfm.type.ClassesDefine;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
class TestMojo {
    private final static String plansDirectory = "./src/test/resources";
    private final static String outputSourceDirectory = "/tmp";
    private final static String outputUtilPackage = null;
    //    private final static String outputUtilPackage = "com.utils";
    //    private final static String[] plans = { "test.yaml" };
//private final static String[] plans = { "define.yaml" };
    //private final static String[] plans = { "B280.yaml" };
    private final static String[] plans = {"SuezTest.yaml"};
    private final static LanguageEnum language = LanguageEnum.java;

    @Test
    void testPlans1() {
        Assertions.assertAll(() -> {
            Yaml yaml = RecordFormatMojo.prepareYaml();

            log.info("Plans directory: " + plansDirectory);
            for (String plan : plans) {
                log.info("Generate from " + plan);
                try (InputStream inputStream = Files.newInputStream(Paths.get(plansDirectory + File.separator + plan))) {
                    ClassesDefine structs;
                    structs = yaml.load(inputStream);
                    LanguageEnum.java.generate(structs, GenerateArgs.builder()
                        .sourceDirectory(outputSourceDirectory)
                        .utilPackage(outputUtilPackage)
                        .build()
                    );
                }
            }
            log.info("Coping templates ...");
            String outputUtilDirectory = Tools.makeDirectory(outputSourceDirectory, outputUtilPackage);
            language.copyTemplate(this.getClass().getClassLoader(), outputUtilDirectory, outputUtilPackage);

            log.info("Done.");

        });
    }

}
