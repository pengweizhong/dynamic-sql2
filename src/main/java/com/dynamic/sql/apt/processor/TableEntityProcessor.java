package com.dynamic.sql.apt.processor;

import com.dynamic.sql.anno.Table;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * mvn clean compile
 * mvn clean test-compile -X -e
 */
@SupportedAnnotationTypes("com.dynamic.sql.anno.Table")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TableEntityProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        this.filer = env.getFiler();
        this.messager = env.getMessager();
        // 这句必须调用
        this.processingEnv = env;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
            if (element.getKind() != ElementKind.CLASS) continue;
            TypeElement typeElement = (TypeElement) element;
            Table sqlEntity = typeElement.getAnnotation(Table.class);
            String className = typeElement.getSimpleName().toString();
            String alias = sqlEntity.alias();
            String pkg = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
            String dslClassName = className + "DSL";

            try {
                generateDslClass(pkg, dslClassName, alias, typeElement);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Failed to write DSL: " + e.getMessage());
            }
        }
        return true;
    }

    private void generateDslClass(String pkg, String className, String alias, TypeElement entityClass) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(pkg).append(";\n\n")
                .append("import java.util.*;\n")
                .append("public class ").append(className).append(" {\n")
                .append("  public static final String TABLE_ALIAS = \"").append(alias).append("\";\n\n");

//        List<String> columnNames = new ArrayList<>();
//        for (Element field : entityClass.getEnclosedElements()) {
//            if (field.getKind() == ElementKind.FIELD) {
//                String fieldName = field.getSimpleName().toString();
//                columnNames.add(fieldName);
//                builder.append("  public static final ColumnRef ")
//                        .append(fieldName.toUpperCase())
//                        .append(" = new ColumnRef(TABLE_ALIAS, \"").append(fieldName).append("\");\n");
//            }
//        }
//
//        // allColumns()
//        builder.append("\n  public static List<ColumnRef> allColumns() {\n")
//                .append("    return Arrays.asList(")
//                .append(columnNames.stream().map(String::toUpperCase).collect(Collectors.joining(", ")))
//                .append(");\n  }\n");

        builder.append("}\n");

        // ColumnRef 类必须你项目中已有，或也一起生成
        JavaFileObject file = filer.createSourceFile(pkg + "." + className);
        try (Writer writer = file.openWriter()) {
            writer.write(builder.toString());
        }
    }
}
