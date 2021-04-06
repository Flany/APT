package com.flany.complier;

import com.flany.api.ARouter;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.flany.api.ARouter"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)

@SupportedOptions("app")
public class ARouterProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        String app = processingEnvironment.getOptions().get("app");
        messager.printMessage(Diagnostic.Kind.NOTE, ">>" + app + ">>");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);
        for (Element element : elements) {//引用注解的类
            MethodSpec mainMethod = MethodSpec.methodBuilder("main")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(String[].class, "args")
                    .addStatement("$T.out.println($S)", System.class, "Hello world！")
                    .build();

            TypeSpec clazz = TypeSpec.classBuilder("TestClass")
                    .addMethod(mainMethod)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .build();

            JavaFile packageTest = JavaFile.builder("com.flany.apt", clazz)
                    .build();

            try {
                packageTest.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
