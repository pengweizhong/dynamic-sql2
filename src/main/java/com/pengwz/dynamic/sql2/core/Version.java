package com.pengwz.dynamic.sql2.core;

public class Version {
    //主版本号
    private static final ThreadLocal<Integer> MAJOR_VERSION = new ThreadLocal<>();
    //次版本号
    private static final ThreadLocal<Integer> MINOR_VERSION = new ThreadLocal<>();
    //补丁号
    private static final ThreadLocal<Integer> PATCH_VERSION = new ThreadLocal<>();

    public static int getMajorVersion() {
        return MAJOR_VERSION.get();
    }

    protected static void setMajorVersion(int majorVersion) {
        MAJOR_VERSION.set(majorVersion);
    }

    public static int getMinorVersion() {
        return MINOR_VERSION.get();
    }

    protected static void setMinorVersion(int minorVersion) {
        MINOR_VERSION.set(minorVersion);

    }

    public static int getPatchVersion() {
        return PATCH_VERSION.get();
    }

    protected static void setPatchVersion(int patchVersion) {
        PATCH_VERSION.set(patchVersion);
    }

    public static void clear() {
        MAJOR_VERSION.remove();
        MINOR_VERSION.remove();
        PATCH_VERSION.remove();
    }

}
