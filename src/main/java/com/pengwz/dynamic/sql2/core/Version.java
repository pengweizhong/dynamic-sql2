package com.pengwz.dynamic.sql2.core;

public class Version {
    private int majorVersion;
    private int minorVersion;
    private int patchVersion;

    public Version(int majorVersion, int minorVersion, int patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }


    public int getMinorVersion() {
        return minorVersion;
    }


    public int getPatchVersion() {
        return patchVersion;
    }


}
