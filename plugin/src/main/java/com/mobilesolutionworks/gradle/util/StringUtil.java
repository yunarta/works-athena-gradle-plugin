package com.mobilesolutionworks.gradle.util;

import javax.annotation.Nonnull;

public class StringUtil {

    private StringUtil() {

    }

    @Nonnull
    public static String lowerCase(@Nonnull String value) {
        return value.toLowerCase();
    }
}
