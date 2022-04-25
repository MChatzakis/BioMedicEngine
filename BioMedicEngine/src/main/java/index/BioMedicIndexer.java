/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import structures.Doc;
import structures.Term;
import commonUtilities.CommonUtilities;
import gr.uoc.csd.hy463.NXMLFileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import lombok.Data;
import mitos.stemmer.Stemmer;

/**
 *
 * @author Manos Chatzakis
 */
@Data
public class BioMedicIndexer {

    private ArrayList<String> stopWords;
    private final String[] stopPoints = {".", ",", "(", ")", "[", "]", "\'", "\"", ";", ":", "?", "*", "&", "#", "@", "-", "!", "~", "<", ">", "{", "}", "=", "|", "\\", "/", "%", "$", "+"};
    private final String[] tagNames = {"PMC ID", "Title", "Abstract", "Body", "Journal", "Publisher", "Authors", "Categories"};

    private TreeMap<String, Term> vocabulary;
    private TreeMap<Integer, Doc> documents;

    private RandomAccessFile documentsRAF;

    private void initialize() {
        stopWords = new ArrayList<>();
        stopWords.addAll(Arrays.asList(stopPoints));

        vocabulary = new TreeMap<>();
        documents = new TreeMap<>();
        Stemmer.Initialize();
    }

    /**
     * A text can contain stopPoints in form "word1, word2". This method removes
     * , from word1
     *
     * @param text
     * @return
     */
    private String formatStopPoints(String text) {

        String formattedText = text; //giati to ekana auto lul
        for (String point : stopPoints) {
            if (CommonUtilities.isNumeric(formattedText) && (point.equals(".") || point.equals(","))) {
                continue;
            }
            formattedText = formattedText.replace(point, "");
        }

        return formattedText;

    }

    private void addTermToMapAction(String currentWord, String currentTag, int docID) {
        Term currentTerm;

        if (!vocabulary.containsKey(currentWord)) {
            currentTerm = new Term(currentWord);
            vocabulary.put(currentWord, currentTerm);
        } else {
            currentTerm = vocabulary.get(currentWord);
        }

        currentTerm.addOccurence(tagNames, currentTag, docID);
    }

    private void processRawContent(String content, String tag, int docID) {

        //content = formatStopPoints(content);
        String delimiter = "\t\n\r\f ";
        StringTokenizer tokenizer = new StringTokenizer(content, delimiter);
        while (tokenizer.hasMoreTokens()) {
            String currentWord = formatStopPoints(tokenizer.nextToken());

            if (stopWords.contains(currentWord) || currentWord.equals("")) {
                continue;
            }

            //now current word is not a stopWord, or a word containg stoppoints, thus we can add it in structure
            addTermToMapAction(Stemmer.Stem(currentWord), tag, docID);
            //addTermToMapAction(currentWord, tag, docID);
        }
    }

    private void createVocabularyFile(String filepath) throws IOException {
        FileWriter vocabWriter = new FileWriter(filepath);
        for (Map.Entry<String, Term> entry : vocabulary.entrySet()) {
            vocabWriter.write(entry.getValue().getValue() + " " + entry.getValue().getDf() + "\n");
        }
        vocabWriter.close();
    }

    private void createDocumentsFile(String filepath) throws IOException {
        FileWriter docWriter = new FileWriter(filepath);
        for (Map.Entry<Integer, Doc> entry : documents.entrySet()) {
            docWriter.write(entry.getValue().getId() + " " + entry.getValue().getPath() + " " + entry.getValue().getNorm() + "\n");
        }
        docWriter.close();
    }

    public BioMedicIndexer() {
        initialize();
    }

    public void loadStopWords(String stopWordsFilePath) throws FileNotFoundException {
        ArrayList<String> fileStopWords = CommonUtilities.getFileContentsByLineUTF_8(stopWordsFilePath);
        stopWords.addAll(fileStopWords);
    }

    public TreeMap<String, Term> processNXMLDirectory(String directoryBasePath) throws IOException {
        Collection<String> filepaths = CommonUtilities.getFilesOfDirectory(directoryBasePath).subList(0, 300);
        int documentCounter = 1;
        System.out.println("Total documents to process: " + filepaths.size());
        System.out.println(filepaths);
        for (String filepath : filepaths) {
            Doc doc = new Doc(documentCounter, filepath);
            documents.put(documentCounter++, doc);
            readNXMLFile(doc);
        }

        System.out.println("Total terms: " + vocabulary.size());

        createVocabularyFile("./collectionIndex/vocabularyFile.txt");
        createDocumentsFile("./collectionIndex/documentsFile.txt");

        //Start creating separate files
        return vocabulary;
    }

    public void indexNXMLDirectory(String directoryBasePath) throws IOException {
        //STEP 1. Create The Doc File
        Collection<String> filepaths = CommonUtilities.getFilesOfDirectory(directoryBasePath);
        int documentCounter = 0;
        documentsRAF = new RandomAccessFile("collectionIndex/documentsFile.txt", "rw");
        documentsRAF.seek(0);
        for (String filepath : filepaths) {
            Doc doc = new Doc(documentCounter, filepath);
            
            doc.setNorm(new File(filepath).length());
            
            documents.put(documentCounter, doc);
            documentCounter++;

            String line2write = doc.getId() + " " + doc.getPath() + " " + doc.getNorm() + "\n";
            documentsRAF.writeUTF(line2write);
            doc.setDocFilePointer(documentsRAF.getFilePointer());
        }

    }

    public void readNXMLFile(Doc doc) throws IOException {
        File example = new File(doc.getPath());
        NXMLFileReader xmlFile = new NXMLFileReader(example);

        //doc.setNorm(example.length());

        //Step 1. Read the raw NXML file contents.
        //We care only for the following tags.
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
            String currentContent = tagContents[i]; //The whole text in tag i
            String currentTag = tagNames[i]; //1-1 correspondence between i.

            //System.out.println(currentTag + " ::: " + currentContent);
            processRawContent(currentContent, currentTag, doc.getId());
        }

        for (String authorInfo : authors) {
            String currentTag = "Authors";
            String currentContent = authorInfo;
            processRawContent(currentContent, currentTag, doc.getId());
        }

        for (String categoryInfo : categories) {
            String currentTag = "Categories";
            String currentContent = categoryInfo;
            processRawContent(currentContent, currentTag, doc.getId());
        }

    }
}
