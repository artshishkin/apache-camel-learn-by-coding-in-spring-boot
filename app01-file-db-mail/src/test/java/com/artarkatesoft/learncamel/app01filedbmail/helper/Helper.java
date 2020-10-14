package com.artarkatesoft.learncamel.app01filedbmail.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class Helper {
    public static void deleteDirectory(Path dirPath) throws IOException {
        if (Files.exists(dirPath))
            Files.walk(dirPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
    }
}
