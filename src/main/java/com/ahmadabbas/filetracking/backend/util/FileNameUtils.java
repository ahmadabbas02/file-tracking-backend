package com.ahmadabbas.filetracking.backend.util;

public class FileNameUtils {
    public static String getFileExtension(String fileName) {
        return "." + fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
