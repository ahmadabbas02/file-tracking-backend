package com.ahmadabbas.filetracking.backend.util;

public class FileNameUtil {
    public static String getFileExtension(String fileName) {
        return "." + fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
