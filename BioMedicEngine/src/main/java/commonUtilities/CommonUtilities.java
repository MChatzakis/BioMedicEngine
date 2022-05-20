/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonUtilities;

import generalStructures.Doc;
import gr.uoc.csd.hy463.NXMLFileReader;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.text.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
//import org.w3c.dom.Document;  
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

/**
 *
 * @author Manos Chatzakis
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

    //from http://www.java2s.com/example/java-utility-method/cosine-similarity/cosine-similarity-double-vec1-double-vec2-f9000.html
    public static double cosineSimilarity(double[] vec1, double[] vec2) {
        double cosim = vectorDot(vec1, vec2) / (vectorNorm(vec1) * vectorNorm(vec2));
        return cosim;
    }

    public static double vectorDot(double[] vec1, double[] vec2) {
        double sum = 0;
        for (int i = 0; i < vec1.length && i < vec2.length; i++) {
            sum += vec1[i] * vec2[i];
        }
        return sum;
    }

    public static double vectorNorm(double[] vec) {
        double sum = 0;
        for (double v : vec) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }

    public static double log2(double N) {
        return (Math.log(N) / Math.log(2));
    }

    public static double getIDF(int df, int N) {
        return log2((N * 1.0) / (df * 1.0));
    }

    public static int KnuthMorrisPrattSearch(char[] pattern, char[] text) {
        int patternSize = pattern.length;
        int textSize = text.length;

        int i = 0, j = 0;

        int[] shift = KnuthMorrisPrattShift(pattern);

        while ((i + patternSize) <= textSize) {
            while (text[i + j] == pattern[j]) {
                j += 1;
                if (j >= patternSize) {
                    return i;
                }
            }

            if (j > 0) {
                i += shift[j - 1];
                j = Math.max(j - shift[j - 1], 0);
            } else {
                i++;
                j = 0;
            }
        }
        return -1;
    }

    public static int[] KnuthMorrisPrattShift(char[] pattern) {
        int patternSize = pattern.length;

        int[] shift = new int[patternSize];
        shift[0] = 1;

        int i = 1, j = 0;

        while ((i + j) < patternSize) {
            if (pattern[i + j] == pattern[j]) {
                shift[i + j] = i;
                j++;
            } else {
                if (j == 0) {
                    shift[i] = i + 1;
                }

                if (j > 0) {
                    i = i + shift[j - 1];
                    j = Math.max(j - shift[j - 1], 0);
                } else {
                    i = i + 1;
                    j = 0;
                }
            }
        }
        return shift;
    }

    public static String getRawNXMLFileContent(String filepath) throws IOException {
        String str = "";

        File example = new File(filepath);
        NXMLFileReader xmlFile = new NXMLFileReader(example);

        str += xmlFile.getPMCID() + " ";
        str += xmlFile.getTitle() + " ";
        str += xmlFile.getAbstr() + " ";
        str += xmlFile.getBody() + " ";
        str += xmlFile.getJournal() + " ";
        str += xmlFile.getPublisher() + " ";

        ArrayList<String> authors = xmlFile.getAuthors();
        for (String a : authors) {
            str += a + " ";
        }
        HashSet<String> categories = xmlFile.getCategories();
        for (String c : categories) {
            str += c + " ";
        }

        return str;
    }

    public static String folderSelectionGUI() {

        String filepath = "";
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select a folder");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            filepath = file.getAbsolutePath();
            System.out.println("The path of the selected folder is: " + filepath);
        }

        return filepath;
    }

    public static String fileSelectionGUI() {

        String filepath = "";
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Select a file");
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            filepath = file.getAbsolutePath();
            System.out.println("The path of the selected file is: " + filepath);
        }

        return filepath; //It returns a string in order to use it easily while creating a file
    }

    public static ArrayList<String> readXML(String filename, String tagName, String sel) {
        ArrayList<String> res = new ArrayList<>();
        try {
            File file = new File(filename);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName(tagName);

            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);
                System.out.println(node.getAttributes().getNamedItem("type"));
                //System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    /*System.out.println("Student id: " + eElement.getElementsByTagName("id").item(0).getTextContent());
                    System.out.println("First Name: " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
                    System.out.println("Last Name: " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
                    System.out.println("Subject: " + eElement.getElementsByTagName("subject").item(0).getTextContent());
                    System.out.println("Marks: " + eElement.getElementsByTagName("marks").item(0).getTextContent());*/
                    String s = eElement.getElementsByTagName(sel).item(0).getTextContent();
                    //System.out.println(s);
                    res.add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public static ArrayList<String> readXMLattr(String filename, String tagName, String sel) {
        ArrayList<String> res = new ArrayList<>();
        try {
            File file = new File(filename);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName(tagName);

            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);
                res.add(node.getAttributes().getNamedItem(sel).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public static String readPMCIDofFile(Doc doc) throws IOException {
        File example = new File(doc.getPath());
        NXMLFileReader xmlFile = new NXMLFileReader(example);

        String pmcid = xmlFile.getPMCID();

        return pmcid;
    }

}
