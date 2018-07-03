package com.mobilesolutionworks.gradle.util;

import javax.annotation.Nonnull;

public class StringUtils {

    private StringUtils() {

    }

    @Nonnull
    public static String lowerCase(@Nonnull String value) {
        return value.toLowerCase();
    }
}
