package com.hzc.nonocontroller.util;

import androidx.databinding.InverseMethod;

public class Converters {
    @InverseMethod("stringToInteger")
    public static String integerToString(Integer value) {
        return value == null ? "" : String.valueOf(value);
    }

    public static Integer stringToInteger(String s) {
        try { return (s == null || s.isEmpty()) ? 0 : Integer.valueOf(s); }
        catch (NumberFormatException e) { return 0; }
    }
}
