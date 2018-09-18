package org.fogbowcloud.cli.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static String readFileToString(String completeFilePath) throws IOException {
        Path path = Paths.get(completeFilePath);
        byte[] fileContent = Files.readAllBytes(path);
        String strContent = new String(fileContent);
        return strContent;
    }
}
