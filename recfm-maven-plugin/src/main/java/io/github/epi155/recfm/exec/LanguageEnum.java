package io.github.epi155.recfm.exec;

import io.github.epi155.recfm.java.ContextJava;
import io.github.epi155.recfm.scala.ContextScala;
import io.github.epi155.recfm.type.ClassesDefine;

public enum LanguageEnum {
    java(new ContextJava()),
    scala(new ContextScala());
    private final LanguageContext context;

    LanguageEnum(LanguageContext context) {
        this.context = context;
    }

    public void generate(ClassesDefine structs, GenerateArgs args) {
        context.generate(structs, args);
    }

    public void copyTemplate(ClassLoader classLoader, String utilDirectory, String utilPackage) {
        context.copyTemplate(classLoader, utilDirectory, utilPackage);
    }

}
