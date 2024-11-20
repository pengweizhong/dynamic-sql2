package com.pengwz.dynamic.sql2.core;

import java.util.Objects;

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

    public int compareTo(Version other) {
        if (this.majorVersion != other.majorVersion) {
            return this.majorVersion - other.majorVersion;
        }
        if (this.minorVersion != other.minorVersion) {
            return this.minorVersion - other.minorVersion;
        }
        return this.patchVersion - other.patchVersion;
    }

    public boolean isGreaterThan(Version other) {
        return this.compareTo(other) > 0;
    }

    public boolean isGreaterThanOrEqual(Version other) {
        return this.compareTo(other) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return majorVersion == version.majorVersion && minorVersion == version.minorVersion && patchVersion == version.patchVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorVersion, minorVersion, patchVersion);
    }

}
