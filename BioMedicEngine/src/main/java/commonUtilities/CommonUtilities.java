/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author manos
 */
public class CommonUtilities {

    public static ArrayList<String> getFilesOfDirectory(String directoryPath) throws IOException {
        ArrayList<String> filepaths = new ArrayList<>();

        Path directory = Paths.get(directoryPath);
        List<Path> files = Files.walk(directory).collect(Collectors.<Path>toList());

        for (Path file : files) {
            if (Files.isDirectory(file)) {
                continue;
            }

            String path = file.toAbsolutePath().toString();

            filepaths.add(path);
        }

        return filepaths;
    }

    public static ArrayList<String> getFileContentsByLineUTF_8(String filePath) throws FileNotFoundException {
        ArrayList<String> lines = new ArrayList<>();

        File inputFile = new File(filePath);
        try {

            FileInputStream fis = new FileInputStream(inputFile);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                lines.add(currentLine);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lines;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
