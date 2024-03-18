package com.ahmadabbas.filetracking.backend.util;

public class FileNameUtils {
    public static String getFileExtension(String fileName) {
        return "." + fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
