/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import commonUtilities.CommonUtilities;
import gr.uoc.csd.hy463.NXMLFileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;
import lombok.Data;

/**
 *
 * @author Manos Chatzakis
 */
@Data
public class BioMedicIndexer {

    private ArrayList<String> stopWords;
    private final String[] stopPoints = {".", ",", "(", ")"};
    private final String[] tagNames = {"PMC ID", "Title", "Abstract", "Body", "Journal", "Publisher", "Authors", "Categories"};

    private void initialize() {
        stopWords = new ArrayList<>();
        stopWords.addAll(Arrays.asList(stopPoints));
    }

    public BioMedicIndexer() {
        initialize();
    }

    public void loadStopWords(String stopWordsFilePath) throws FileNotFoundException {
        ArrayList<String> fileStopWords = CommonUtilities.getFileContentsByLineUTF_8(stopWordsFilePath);
        stopWords.addAll(fileStopWords);
    }

    public String formatStopPoints(String text) {
        String formattedText = text; //giati to ekana auto lul
        for (String point : stopPoints) {
            formattedText = text.replaceAll("\\" + point, " " + point + " ");
        }
        return formattedText;
    }

    public void addTermToMapAction(String currentWord, String currentTag, HashMap<String, Term> termsOccurences) {
        Term currentTerm;
        if (termsOccurences.containsKey(currentWord)) {
            currentTerm = termsOccurences.get(currentWord);
        } else {
            currentTerm = new Term(currentWord);
            currentTerm.initializeTagOccurences(tagNames);

            termsOccurences.put(currentWord, currentTerm);
        }

        currentTerm.increaseOccurenceOfWordInTag(currentTag);
    }

    public void processRawContent(String content, String tag, HashMap<String, Term> termsOccurences) {
        String currentContent = formatStopPoints(content);

        String delimiter = "\t\n\r\f ";
        StringTokenizer tokenizer = new StringTokenizer(currentContent, delimiter);
        while (tokenizer.hasMoreTokens()) {
            String currentWord = tokenizer.nextToken();

            if (stopWords.contains(currentWord)) {
                continue;
            }

            addTermToMapAction(currentWord, tag, termsOccurences);
        }
    }

    public HashMap<String, Term> processNXMLDirectory(String directoryBasePath) throws IOException {
        HashMap<String, Term> termsOccurences = new HashMap<>();
        ArrayList<String> filepaths = CommonUtilities.getFilesOfDirectory(directoryBasePath);
        //String currPath = "./sample/MiniCollection/MiniCollection/treatment/Topic_27/0/1936313.nxml";
        //readNXMLFile(currPath, termsOccurences);
        System.out.println("Total documents to process: " + filepaths.size());
        //for (String filepath : filepaths) {
            //readNXMLFile(filepath, termsOccurences);
        //}

        System.out.println("Total terms: " + termsOccurences.size());

        return termsOccurences;
    }

    public void readNXMLFile(String filepath, HashMap<String, Term> termsOccurences) throws IOException {
        File example = new File(filepath);
        NXMLFileReader xmlFile = new NXMLFileReader(example);

        String pmcid = xmlFile.getPMCID();
        String title = xmlFile.getTitle();
        String abstr = xmlFile.getAbstr();
        String body = xmlFile.getBody();
        String journal = xmlFile.getJournal();
        String publisher = xmlFile.getPublisher();

        ArrayList<String> authors = xmlFile.getAuthors();
        HashSet<String> categories = xmlFile.getCategories();

        String[] tagContents = {pmcid, title, abstr, body, journal, publisher};

        for (int i = 0; i < tagContents.length; i++) {
            String currentContent = tagContents[i];
            String currentTag = tagNames[i];
            processRawContent(currentContent, currentTag, termsOccurences);
        }

        for (String authorInfo : authors) {
            String currentTag = "Authors";
            String currentContent = authorInfo;
            processRawContent(currentContent, currentTag, termsOccurences);

        }

        for (String categoryInfo : categories) {
            String currentTag = "Categories";
            String currentContent = categoryInfo;
            processRawContent(currentContent, currentTag, termsOccurences);
        }

    }

    public void readNXMLFile(String filepath) throws IOException {
        File example = new File(filepath);
        NXMLFileReader xmlFile = new NXMLFileReader(example);
        String pmcid = xmlFile.getPMCID();
        String title = xmlFile.getTitle();
        String abstr = xmlFile.getAbstr();
        String body = xmlFile.getBody();
        String journal = xmlFile.getJournal();
        String publisher = xmlFile.getPublisher();
        ArrayList<String> authors = xmlFile.getAuthors();
        HashSet<String> categories = xmlFile.getCategories();

        System.out.println("- PMC ID: " + pmcid);
        System.out.println("- Title: " + title);
        System.out.println("- Abstract: " + abstr);
        System.out.println("- Body: " + body);
        System.out.println("- Journal: " + journal);
        System.out.println("- Publisher: " + publisher);
        System.out.println("- Authors: " + authors);
        System.out.println("- Categories: " + categories);

        String[] tagNames = {"PMC ID", "Title", "Abstract", "Body", "Journal", "Publisher", "Authors", "Categories"};
        String[] tagContents = {pmcid, title, abstr, body, journal, publisher};
        HashMap<String, Term> termsOccurences = new HashMap<>();

        for (int i = 0; i < tagContents.length; i++) {
            String currentContent = tagContents[i];
            String currentTag = tagNames[i];

            currentContent = formatStopPoints(currentContent);

            String delimiter = "\t\n\r\f ";
            StringTokenizer tokenizer = new StringTokenizer(currentContent, delimiter);
            while (tokenizer.hasMoreTokens()) {
                String currentWord = tokenizer.nextToken();
                Term currentTerm;

                if (stopWords.contains(currentWord)) {
                    continue;
                }

                if (termsOccurences.containsKey(currentWord)) {
                    currentTerm = termsOccurences.get(currentWord);
                } else {
                    currentTerm = new Term(currentWord);
                    currentTerm.initializeTagOccurences(tagNames);

                    termsOccurences.put(currentWord, currentTerm);
                }

                currentTerm.increaseOccurenceOfWordInTag(currentTag);
            }

            for (Map.Entry<String, Term> entry : termsOccurences.entrySet()) {
                System.out.println(entry.getValue());
            }
            System.out.println("Total terms in file: " + termsOccurences.size());

        }

    }
}
