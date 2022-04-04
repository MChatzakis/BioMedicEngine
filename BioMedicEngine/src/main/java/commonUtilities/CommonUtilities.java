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
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 *
 * @author manos
 */
public class CommonUtilities {

    public static ArrayList<String> getFilesOfDirectory(String dir) {
        return null;
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

}
