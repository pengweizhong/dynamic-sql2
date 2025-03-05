package com.dynamic.sql.utils;

import com.dynamic.sql.core.Fn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ReflectUtils {
    public static final Pattern GET_PATTERN = Pattern.compile("^get[A-Z].*");
    public static final Pattern START_UPPER_PATTERN = Pattern.compile("^[A-Z].*");
    public static final Pattern IS_PATTERN = Pattern.compile("^is[A-Z].*");
    private static final Logger log = LoggerFactory.getLogger(ReflectUtils.class);

    private ReflectUtils() {
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        return getAllFields(clazz, null);
    }

    /**
     * 获取指定类中的所有字段
     *
     * @param clazz       指定类
     * @param filterRules 自定义过滤规则,只选择匹配到的属性
     * @return 返回字段集合
     */
    public static List<Field> getAllFields(Class<?> clazz, Function<Field, Boolean> filterRules) {
        List<Field> fields = new ArrayList<>();
        collectFields(clazz, fields, filterRules);
        return fields;
    }

    private static void collectFields(Class<?> clazz, List<Field> fields, Function<Field, Boolean> filterRules) {
        if (clazz == null || clazz.equals(Object.class)) {
            return;
        }
        // 获取当前类的所有字段
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {//NOSONAR
            // 过滤字段
            if (filterRules == null) {
                fields.add(field);
                continue;
            }
            if (Boolean.TRUE.equals(filterRules.apply(field))) {
                fields.add(field);
            }
        }
        // 递归处理父类
        collectFields(clazz.getSuperclass(), fields, filterRules);
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

    /**
     * 获取指定类的泛型类型。
     *
     * @param clazz 要解析的类
     * @return 泛型类型的 Class 对象列表
     */
    public static List<Class<?>> getGenericTypes(Class<?> clazz) {
        return getGenericTypes(clazz, (Class<?>) null);
    }

    /**
     * 获取指定类的泛型类型。
     * 如果传入了 `filterRawType` 参数，则仅返回匹配的原始类型（Raw Type）所关联的泛型类型。
     *
     * @param clazz         要解析的类
     * @param filterRawType （可选）用于筛选的原始类型（Raw Type），例如接口或类的类型
     * @return 泛型类型的 Class 对象列表
     */
    public static List<Class<?>> getGenericTypes(Class<?> clazz, Class<?>... filterRawType) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        ArrayList<Class<?>> types = new ArrayList<>();
        List<Class<?>> filterRawTypeList = new ArrayList<>();
        if (filterRawType != null && filterRawType.length > 0) {
            filterRawTypeList.addAll(Arrays.asList(filterRawType));
        }
        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                // 返回泛型 的实际类型
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                for (Type actualTypeArgument : actualTypeArguments) {
                    if (filterRawTypeList.isEmpty()) {
                        types.add((Class<?>) actualTypeArgument);
                    } else if (filterRawTypeList.contains((Class<?>) parameterizedType.getRawType())) {
                        types.add((Class<?>) actualTypeArgument);
                    }
                }
            }
        }
        return types;
    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers()))
                && !field.isAccessible()) {
            field.setAccessible(true);//NOSONAR
        }
    }

    public static void setFieldValue(Object target, Field field, Object value) {
        try {
            if (!Modifier.isPublic(field.getModifiers())) {
                makeAccessible(field);
            }
            field.set(target, value);//NOSONAR
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);//NOSONAR
        }
    }

    public static Object getFieldValue(Object target, Field field) {
        try {
            if (!Modifier.isPublic(field.getModifiers())) {
                makeAccessible(field);
            }
            return field.get(target);//NOSONAR
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);//NOSONAR
        }
    }

    public static Class<?> loadClass(String classCanonicalName) {
        try {
            return Class.forName(classCanonicalName);
        } catch (ClassNotFoundException e) {
            try {
                ClassLoader.getSystemClassLoader().loadClass(classCanonicalName);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        throw new RuntimeException(new ClassNotFoundException(classCanonicalName));
    }

//    public static <T> T instance(Class<T> clazz, Object... args) {
//        try {
//            // 获取构造函数参数的类型
//            Class<?>[] argTypes = new Class[args.length];
//            for (int i = 0; i < args.length; i++) {
//                argTypes[i] = args[i].getClass();
//            }
//            Constructor<T> constructor = clazz.getDeclaredConstructor(argTypes);
//            constructor.setAccessible(true);//NOSONAR
//            return constructor.newInstance(args);
//        } catch (Exception e) {
//            throw new RuntimeException("Unable to create instance of class: " + clazz.getName(), e);//NOSONAR
//        }
//    }

    @SuppressWarnings("all")
    public static <T> T instance(Class<T> clazz, Object... args) {
        try {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            Constructor<T> matchedConstructor = null;
            Object[] adjustedArgs = null;

            // 遍历所有构造方法，寻找匹配的
            for (Constructor<?> constructor : constructors) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                if (isMatchingConstructor(paramTypes, args)) {
                    matchedConstructor = (Constructor<T>) constructor;
                    adjustedArgs = adjustArguments(paramTypes, args);
                    break;
                }
            }

            if (matchedConstructor == null) {
                throw new NoSuchMethodException("No matching constructor found for " + clazz.getName() +
                        " with args: " + Arrays.toString(args));
            }

            matchedConstructor.setAccessible(true); // 允许访问私有构造方法
            return matchedConstructor.newInstance(adjustedArgs);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Constructor not found for class: " + clazz.getName(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create instance of class: " + clazz.getName(), e);
        }
    }

    // 检查构造方法是否匹配传入参数
    private static boolean isMatchingConstructor(Class<?>[] paramTypes, Object[] args) {
        if (paramTypes.length == 0 && args.length == 0) {
            return true; // 无参构造方法
        }
        // 处理可变参数的情况
        boolean isVarArgs = paramTypes.length > 0 && paramTypes[paramTypes.length - 1].isArray();
        int fixedParamCount = isVarArgs ? paramTypes.length - 1 : paramTypes.length;
        // 参数数量检查
        if (!isVarArgs && paramTypes.length != args.length) {
            return false;
        }
        if (isVarArgs && args.length < fixedParamCount) {
            return false;
        }
        // 检查固定参数部分
        for (int i = 0; i < fixedParamCount; i++) {
            if (!isAssignable(paramTypes[i], args[i])) {
                return false;
            }
        }
        // 检查可变参数部分
        if (isVarArgs) {
            Class<?> varArgType = paramTypes[paramTypes.length - 1].getComponentType();
            for (int i = fixedParamCount; i < args.length; i++) {
                if (!isAssignable(varArgType, args[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    // 调整参数以匹配构造方法（包括打包可变参数）
    private static Object[] adjustArguments(Class<?>[] paramTypes, Object[] args) {
        boolean isVarArgs = paramTypes.length > 0 && paramTypes[paramTypes.length - 1].isArray();
        if (!isVarArgs || args.length <= paramTypes.length) {
            return args; // 无需调整
        }

        // 处理可变参数
        int fixedParamCount = paramTypes.length - 1;
        Class<?> varArgType = paramTypes[paramTypes.length - 1].getComponentType();
        Object[] adjustedArgs = new Object[paramTypes.length];

        // 复制固定参数
        System.arraycopy(args, 0, adjustedArgs, 0, fixedParamCount);

        // 打包可变参数为数组
        int varArgLength = args.length - fixedParamCount;
        Object varArgArray = Array.newInstance(varArgType, varArgLength);
        for (int i = 0; i < varArgLength; i++) {
            Array.set(varArgArray, i, args[fixedParamCount + i]);
        }
        adjustedArgs[fixedParamCount] = varArgArray;

        return adjustedArgs;
    }

    // 检查类型是否可赋值（考虑基本类型和包装类型）
    private static boolean isAssignable(Class<?> paramType, Object arg) {
        if (arg == null) {
            return !paramType.isPrimitive(); // null 可以赋值给引用类型，但不能给基本类型
        }
        Class<?> argType = arg.getClass();
        // 直接类型匹配
        if (paramType.isAssignableFrom(argType)) {
            return true;
        }
        // 处理基本类型和包装类型
        if (paramType.isPrimitive()) {
            if (paramType == int.class && argType == Integer.class) return true;
            if (paramType == double.class && argType == Double.class) return true;
            if (paramType == boolean.class && argType == Boolean.class) return true;
            if (paramType == long.class && argType == Long.class) return true;
            if (paramType == float.class && argType == Float.class) return true;
            if (paramType == short.class && argType == Short.class) return true;
            if (paramType == byte.class && argType == Byte.class) return true;
            if (paramType == char.class && argType == Character.class) return true;
        }
        return false;
    }
}
