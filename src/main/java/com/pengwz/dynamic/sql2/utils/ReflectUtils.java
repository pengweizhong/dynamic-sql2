package com.pengwz.dynamic.sql2.utils;

import com.pengwz.dynamic.sql2.core.Fn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ReflectUtils {
    public static final Pattern GET_PATTERN = Pattern.compile("^get[A-Z].*");
    public static final Pattern START_UPPER_PATTERN = Pattern.compile("^[A-Z].*");
    public static final Pattern IS_PATTERN = Pattern.compile("^is[A-Z].*");
    private static final Logger log = LoggerFactory.getLogger(ReflectUtils.class);

    private ReflectUtils() {
    }

    /**
     * 获取指定类中的所有字段
     *
     * @param clazz          指定类
     * @param filterModifier 如果需要过滤类型，建议使用{@link Modifier}
     * @return 返回字段集合
     */
    public static List<Field> getAllFields(Class<?> clazz, int... filterModifier) {
        List<Field> fields = new ArrayList<>();
        collectFields(clazz, fields, filterModifier);
        return fields;
    }

    private static void collectFields(Class<?> clazz, List<Field> fields, int... filterModifier) {
        if (clazz == null || clazz.equals(Object.class)) {
            return;
        }
        // 获取当前类的所有字段
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {//NOSONAR
            // 过滤字段
            if (filterModifier == null || filterModifier.length == 0) {
                fields.add(field);
                continue;
            }
            int modifiers = field.getModifiers();
            if (Arrays.stream(filterModifier).anyMatch(mod -> modifiers == mod)) {
                continue;
            }
            fields.add(field);
        }
        // 递归处理父类
        collectFields(clazz.getSuperclass(), fields);
    }

    /**
     * 根据传入的函数式接口 `Fn` 获取对应的字段名。
     *
     * <p>该方法通过序列化 Lambda 表达式，获取实现方法的名称，并根据 Java Bean 规范进行处理。
     * 主要处理以下几种情况：
     * <ul>
     *     <li>方法名以 "get" 开头：去掉前缀 "get" 并转换为字段名</li>
     *     <li>方法名以 "is" 开头（布尔类型）：去掉前缀 "is" 并转换为字段名</li>
     *     <li>没有匹配到 "get" 或 "is" 前缀时，直接裁剪掉可能存在的 "get" 前缀</li>
     * </ul>
     *
     * <p>此外，该方法会将字段名的首字母小写，确保符合常规的 Java 命名规范。
     *
     * @param fn 代表字段的函数式接口
     * @return 字段名称
     */
    @SuppressWarnings("all")
    public static String fnToFieldName(Fn fn) {
        SerializedLambda serializedLambda = serializedLambda(fn);
        String getter = serializedLambda.getImplMethodName();
        if (GET_PATTERN.matcher(getter).matches()) {
            getter = getter.substring(3);
        } else if (IS_PATTERN.matcher(getter).matches()) {
            getter = getter.substring(2);
        } else {
            //直接裁剪
            if (getter.startsWith("get")) {
                getter = getter.substring(3);
            }
        }
        getter = Introspector.decapitalize(getter);
        if (START_UPPER_PATTERN.matcher(getter).matches()) {
            String begin = getter.substring(0, 1).toLowerCase();
            String end = getter.substring(1);
            getter = begin + end;
        }
        return getter;
    }

    /**
     * 从 SerializedLambda 获取原始类的全限定名称。
     *
     * @param fn 代表字段的函数式接口
     * @return 原始类的全限定名称
     */
    @SuppressWarnings("all")
    public static String getOriginalClassCanonicalName(Fn fn) {
        // 获取 SerializedLambda
        SerializedLambda serializedLambda = serializedLambda(fn);
        // 获取实现类的内部名称
        String implClass = serializedLambda.getImplClass();
        // 判断是否为空或者不符合标准的内部类名称
        if (implClass == null || implClass.isEmpty()) {
            throw new IllegalStateException("Unable to obtain the implementation class");
        }
        // 将 "/" 替换为 "."，得到标准的 Java 类名
        return implClass.replace("/", ".");
    }

    /**
     * 从给定的函数式接口 `Fn` 中获取返回值的类型。
     *
     * <p>该方法通过 `SerializedLambda` 获取函数的签名信息，解析其中的返回值类型描述符，并将其转换为对应的 Java 类。
     *
     * @param <C> 返回值的类型
     * @param fn  代表字段的函数式接口
     * @return 返回值类型的 `Class` 对象
     * @throws RuntimeException 如果无法找到对应的类
     */
    @SuppressWarnings("all")
    public static <C> Class<C> getReturnTypeFromSignature(Fn fn) {
        SerializedLambda serializedLambda = serializedLambda(fn);
        String implMethodSignature = serializedLambda.getImplMethodSignature();
        // 移除方法签名中的参数部分，即括号"()"，括号内代表参数类型，括号后代表返回值类型
        String returnTypeDescriptor = implMethodSignature.substring(implMethodSignature.indexOf(')') + 1);
        // 将描述符转换为类名
        String className = descriptorToClassName(returnTypeDescriptor);
        try {
            return (Class<C>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find class for name: " + className, e);
        }
    }

    /**
     * 将类型描述符转换为对应的 Java 类全限定名称。
     *
     * @param descriptor 类型描述符，例如 `Ljava/time/LocalDate;`
     * @return 对应的 Java 类全限定名称，例如 `java.time.LocalDate`
     * @throws IllegalArgumentException 如果描述符不符合支持的格式
     */
    @SuppressWarnings("all")
    private static String descriptorToClassName(String descriptor) {
        if (descriptor.startsWith("L") && descriptor.endsWith(";")) {
            // 处理对象类型描述符，将L开头和;结尾去除，并将'/'替换为'.'
            return descriptor.substring(1, descriptor.length() - 1).replace('/', '.');
        }
        throw new IllegalArgumentException("Unsupported descriptor: " + descriptor);
    }

    /**
     * 通过反射获取传入的函数式接口 `Fn` 的 `SerializedLambda` 实例。
     *
     * @param fn 代表 Lambda 表达式的函数式接口
     * @return `SerializedLambda` 对象，包含 Lambda 的元信息
     * @throws RuntimeException 如果反射操作失败
     */
    @SuppressWarnings("all")
    private static SerializedLambda serializedLambda(Fn fn) {
        try {
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            return (SerializedLambda) method.invoke(fn);
        } catch (ReflectiveOperationException e) {
            log.warn(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
