package com.dynamic.sql.core;

import org.junit.jupiter.api.Test;

class VersionTest {

    @Test
    void isGreaterThan() {
        Version currentVersion = new Version(8, 0, 0);
        System.out.println(currentVersion.isGreaterThan(new Version(1, 0, 0)));
        System.out.println(currentVersion.isGreaterThan(new Version(8, 0, 0)));
        System.out.println(currentVersion.isGreaterThan(new Version(8, 0, 19)));
        System.out.println(currentVersion.isGreaterThan(new Version(8, 1, 0)));
        System.out.println(currentVersion.isGreaterThanOrEqual(new Version(8, 0, 0)));
    }

    @Test
    void isGreaterThanOrEqual() {
    }
}