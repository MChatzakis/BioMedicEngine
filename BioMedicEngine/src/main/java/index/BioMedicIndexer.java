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

    private final int PARTIAL_INDEX_THRESHOLD = 20000;

    private ArrayList<String> stopWords;
    private final String[] stopPoints = {".", ",", "(", ")", "[", "]", "\'", "\"", ";", ":", "?", "*", "&", "#", "@", "-", "!", "~", "<", ">", "{", "}", "=", "|", "\\", "/", "%", "$", "+"};
    private final String[] tagNames = {"PMC ID", "Title", "Abstract", "Body", "Journal", "Publisher", "Authors", "Categories"};

    private TreeMap<String, Term> vocabulary;
    private ArrayList<Doc> documents;

    private void initialize() {
        stopWords = new ArrayList<>();
        stopWords.addAll(Arrays.asList(stopPoints));

        vocabulary = new TreeMap<>();
        //documents = new TreeMap<>();
        documents = new ArrayList<>();
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

    private void addTermToMapAction(String currentWord, String currentTag, int docID, int positionInTag) {
        Term currentTerm;

        if (!vocabulary.containsKey(currentWord)) {
            currentTerm = new Term(currentWord);
            vocabulary.put(currentWord, currentTerm);
        } else {
            currentTerm = vocabulary.get(currentWord);
        }

        currentTerm.addOccurence(tagNames, currentTag, docID, positionInTag);
    }

    private void processRawContent(String content, String tag, int docID) {

        int currentWordPosition = 0;

        String delimiter = "\t\n\r\f ";
        StringTokenizer tokenizer = new StringTokenizer(content, delimiter);
        while (tokenizer.hasMoreTokens()) {
            String currentWord = formatStopPoints(tokenizer.nextToken());

            currentWordPosition++;

            if (stopWords.contains(currentWord) || currentWord.equals("")) {
                continue;
            }

            addTermToMapAction(Stemmer.Stem(currentWord), tag, docID, currentWordPosition - 1);
        }
    }

    private void calculateTFofTermsOfDocument(int docID) {
        int maxOcc = 0;
        for (Map.Entry<String, Term> cterm : vocabulary.entrySet()) {
            Term term = cterm.getValue();
            int termOcc = term.calculateOccurencesInDoc(docID);

            if (termOcc >= maxOcc) {
                maxOcc = termOcc;
            }

        }

        for (Map.Entry<String, Term> cterm : vocabulary.entrySet()) {
            Term term = cterm.getValue();
            term.calculateTFinDoc(docID, maxOcc);
        }
    }

    private void createPartialFiles(int partialFileCounter, String basePath, ArrayList<String> filenames) throws FileNotFoundException, IOException {
        String filenameV = "vocab" + partialFileCounter + ".txt";
        String filenameP = "post" + partialFileCounter + ".txt";

        RandomAccessFile partialVocab = new RandomAccessFile(basePath + filenameV, "rw"); //"collectionIndex/partialIndexing/
        partialVocab.seek(0);

        RandomAccessFile partialPost = new RandomAccessFile(basePath + filenameP, "rw");
        partialPost.seek(0);

        for (Map.Entry<String, Term> voc : vocabulary.entrySet()) {
            Term term = voc.getValue();
            String val = term.getValue();
            int df = term.getDf();

            long ptr = partialPost.getFilePointer();
            TreeMap<Integer, HashMap<String, ArrayList<Integer>>> perDocumentTagPositions = term.getPerDocumentTagPositions();
            for (Map.Entry<Integer, HashMap<String, ArrayList<Integer>>> docTagPos : perDocumentTagPositions.entrySet()) {
                int docID = docTagPos.getKey();
                double tf = term.getPerDocumentTF().get(docID);
                String positionTags = "";
                HashMap<String, ArrayList<Integer>> positions = docTagPos.getValue();
                for (Map.Entry<String, ArrayList<Integer>> pos : positions.entrySet()) {
                    String tag = pos.getKey();
                    ArrayList<Integer> poses = pos.getValue();
                    positionTags += "[" + tag + "=";
                    for (int p : poses) {
                        positionTags += p + ",";
                    }
                    positionTags += "]";
                }

                String postLine = docID + " " + tf + " " + positionTags + "\n";
                partialPost.writeUTF(postLine);
            }

            String vocabLine = val + " " + df + " " + ptr + "\n";
            partialVocab.writeUTF(vocabLine);
        }

        partialPost.close();
        partialVocab.close();

        filenames.add(filenameV);

        vocabulary.clear();

        System.gc();
    }

    private void mergePartialFiles(ArrayList<String> vocabFileNames, String vocab1, String vocab2) {
        /*String partialVocabName = currentVocabFilename;
        String partialPostName = currentVocabFilename.replace("vocab", "post"); //correspondence!

        RandomAccessFile partialVocabRAF = new RandomAccessFile(partialFilesDirectory + partialVocabName, "r");
        RandomAccessFile partialPostRAF = new RandomAccessFile(partialFilesDirectory + partialPostName, "r");
        partialVocabRAF.close();
        partialPostRAF.close();*/
    }

    private void mergePartialFiles(String partialFilesDirectory, ArrayList<String> vocabFileNames, String outputDirectoryPath) throws FileNotFoundException {

        int index = 0;
        int counter = vocabFileNames.size();
        while (!vocabFileNames.isEmpty()) {

            if (vocabFileNames.size() == 1) {
                break;
            }

            String currentVocabFilename = vocabFileNames.get(0);
            vocabFileNames.remove(0);

        }

    }

    public BioMedicIndexer() {
        initialize();
    }

    public void loadStopWords(String stopWordsFilePath) throws FileNotFoundException {
        ArrayList<String> fileStopWords = CommonUtilities.getFileContentsByLineUTF_8(stopWordsFilePath);
        stopWords.addAll(fileStopWords);
    }

    public void indexNXMLDirectory(String directoryBasePath, String outputDirectoryPath) throws IOException {

        long startTime = System.nanoTime();

        String documentsFilepath = outputDirectoryPath + "documentFile.txt";
        String partialFilesDirectory = "collectionIndex/partialIndexing/";

        Collection<String> filepaths = CommonUtilities.getFilesOfDirectory(directoryBasePath);
        ArrayList<String> partialVocabsFilenames = new ArrayList<>();

        int documentCounter = 0;
        int partialCounter = 0;

        RandomAccessFile documentsRAF = new RandomAccessFile(documentsFilepath, "rw");
        documentsRAF.seek(0);

        System.out.println("BioMedic Indexer started created the partial files");
        for (String filepath : filepaths) {
            Doc doc = new Doc(documentCounter, filepath);

            doc.setDocFilePointer(documentsRAF.getFilePointer());
            doc.setNorm(new File(filepath).length());

            if (vocabulary.size() >= PARTIAL_INDEX_THRESHOLD) {
                createPartialFiles(partialCounter++, partialFilesDirectory, partialVocabsFilenames);
            }

            readNXMLFile(doc);
            documentCounter++;

            if (documentCounter % 1000 == 0) {
                System.out.println("Proccessed " + documentCounter + " of " + filepaths.size() + " documents. Used Memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000.0 + " MBytes");
            }

            //write stuff to file
            String docLine = doc.getId() + " " + doc.getPath() + " " + doc.getNorm() + "\n";
            documentsRAF.writeUTF(docLine);
        }

        documentsRAF.close();
        System.out.println("BioMedic Indexer created the partial files. Procceeding to merging phase.");

        //here, merge the shiet
        mergePartialFiles(partialFilesDirectory, partialVocabsFilenames, outputDirectoryPath);

        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;

        //End Logging
        System.out.println("========= BioMedic Indexer Results =========");
        System.out.println("Threshold of terms: " + PARTIAL_INDEX_THRESHOLD);
        System.out.println("Directory of documents indexed: " + directoryBasePath);
        System.out.println("Directory of indexer output: " + outputDirectoryPath);
        System.out.println("Total Documents Indexed: " + documentCounter);
        System.out.println("Total Time Elapsed (in seconds): " + timeElapsed / 1000000000.0);
        System.out.println("=======================================");

    }

    public void readNXMLFile(Doc doc) throws IOException {
        File example = new File(doc.getPath());
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
            String currentContent = tagContents[i]; //The whole text in tag i
            String currentTag = tagNames[i]; //1-1 correspondence between i.
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

        calculateTFofTermsOfDocument(doc.getId());

    }
}
