package index;

import generalStructures.Doc;
import commonUtilities.CommonUtilities;
import commonUtilities.RafPrinter;
import generalStructures.IndexResult;
import gr.uoc.csd.hy463.NXMLFileReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
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
import plot.PlotGenerator;
import plot.PlotPoint;
import vectorModel.VectorModel;

/**
 * BioMedicIndexer class contains methods to index a directory.
 *
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
@Data
public class BioMedicIndexer {

    private final int PARTIAL_INDEX_THRESHOLD = 15000;
    private final int PARTIAL_INDEX_LOGGING_POINT = 5000;

    private ArrayList<String> stopWords;
    private final String[] stopPoints = {".", ",", "(", ")", "[", "]", "\'", "\"", ";", ":", "?", "*", "&", "#", "@", "-", "!", "~", "<", ">", "{", "}", "=", "|", "\\", "/", "%", "$", "+"};
    private final String[] tagNames = {"PMC&ID", "Title", "Abstract", "Body", "Journal", "Publisher", "Authors", "Categories"};

    private TreeMap<String, IndexTerm> vocabulary;
    private TreeMap<Integer, Long> docPointerPairs;

    private void initialize() {
        stopWords = new ArrayList<>();
        stopWords.addAll(Arrays.asList(stopPoints));

        vocabulary = new TreeMap<>();
        docPointerPairs = new TreeMap<>();
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
        IndexTerm currentTerm;

        if (!vocabulary.containsKey(currentWord)) {
            currentTerm = new IndexTerm(currentWord);
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
        for (Map.Entry<String, IndexTerm> cterm : vocabulary.entrySet()) {
            IndexTerm term = cterm.getValue();
            int termOcc = term.calculateOccurencesInDoc(docID);

            if (termOcc >= maxOcc) {
                maxOcc = termOcc;
            }

        }

        for (Map.Entry<String, IndexTerm> cterm : vocabulary.entrySet()) {
            IndexTerm term = cterm.getValue();
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

        for (Map.Entry<String, IndexTerm> voc : vocabulary.entrySet()) {
            IndexTerm term = voc.getValue();
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
                    positionTags += "" + tag + "=";
                    for (int p : poses) {
                        positionTags += p + ",";
                    }
                    positionTags += "_";
                }

                if (docPointerPairs.get(docID) == null) {
                    //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    //System.out.println(docID);
                    System.out.println(docPointerPairs);
                    System.exit(-1);
                }

                String postLine = docID + " " + tf + " " + positionTags + " " + docPointerPairs.get(docID) + " \n";
                partialPost.writeUTF(postLine);
            }

            String vocabLine = val + " " + df + " " + ptr + " \n";
            partialVocab.writeUTF(vocabLine);
            partialPost.writeUTF("#stop\n");
        }

        partialVocab.writeUTF("#end");
        partialPost.writeUTF("#end");

        partialPost.close();
        partialVocab.close();

        filenames.add(filenameV);

        vocabulary.clear();

        System.gc(); //den perimena pote oti tha to kanw auto...
    }

    private void copyContentsToRAF(String[] vcontents, RandomAccessFile pos1, RandomAccessFile vocNew, RandomAccessFile postNew) throws IOException {
        String term = vcontents[0];
        String df = vcontents[1];

        long newPtr = postNew.getFilePointer();
        long currPtr = Long.parseLong(vcontents[2]);

        vocNew.writeUTF(term + " " + df + " " + newPtr + " \n");
        String line;
        pos1.seek(currPtr);
        while ((line = pos1.readUTF()) != null) {
            if (line.equals("#end") || line.equals("#stop\n")) {
                break;
            }
            //System.out.println("[]->" + line);
            String[] contents = line.split(" ");

            String doc = contents[0];
            String tf = contents[1];
            String pos = contents[2];

            //postNew.writeUTF(doc + " " + tf + " " + pos + " \n");
            postNew.writeUTF(line);
        }

        postNew.writeUTF("#stop\n");
    }

    private void mergeContentsAndCopyToRAF(String[] contentsV1, String[] contentsV2, RandomAccessFile pos1, RandomAccessFile pos2, RandomAccessFile vocNew, RandomAccessFile postNew) throws IOException {
        String term = contentsV1[0]; //or V2

        int df1 = Integer.parseInt(contentsV1[1]);
        long ptr1 = Long.parseLong(contentsV1[2]);

        int df2 = Integer.parseInt(contentsV2[1]);
        long ptr2 = Long.parseLong(contentsV2[2]);

        long newPtr = postNew.getFilePointer();
        String line1 = "", line2 = "";

        pos1.seek(ptr1);
        pos2.seek(ptr2);
        line1 = pos1.readUTF();
        line2 = pos2.readUTF();
        while (!line1.equals("#stop\n") && !line2.equals("#stop\n")) {

            if (line1.equals("#end") || line2.equals("#end")) {
                break;
            }

            String[] conts1 = line1.split(" ");
            String[] conts2 = line2.split(" ");

            int docID1 = Integer.parseInt(conts1[0]);
            int docID2 = Integer.parseInt(conts2[0]);

            if (docID1 < docID2) {
                postNew.writeUTF(line1);
                line1 = pos1.readUTF();
            } else {
                postNew.writeUTF(line2);
                line2 = pos2.readUTF();
            }

            //line1 = pos1.readUTF();
            //line2 = pos2.readUTF();
        }

        while (!line1.equals("#stop\n")) {
            if (line1.equals("#end")) {
                break;
            }

            postNew.writeUTF(line1);
            line1 = pos1.readUTF();
        }

        while (!line2.equals("#stop\n")) {
            if (line2.equals("#end")) {
                break;
            }

            postNew.writeUTF(line2);
            line2 = pos2.readUTF();
        }

        postNew.writeUTF("#stop\n");
        vocNew.writeUTF(term + " " + (df1 + df2) + " " + newPtr + " \n");
    }

    private void mergePartialFiles(ArrayList<String> vocabFileNames, String vocab1, String vocab2, String dir, int counter) throws FileNotFoundException, IOException {
        System.out.println(">>Merging pair " + vocab1 + " and " + vocab2);

        String newVocabFilename = "m_vocab" + counter + ".txt";

        RandomAccessFile voc1 = new RandomAccessFile(vocab1, "r");
        RandomAccessFile post1 = new RandomAccessFile(vocab1.replace("vocab", "post"), "r");

        RandomAccessFile voc2 = new RandomAccessFile(vocab2, "r");
        RandomAccessFile post2 = new RandomAccessFile(vocab2.replace("vocab", "post"), "r");

        RandomAccessFile newVoc = new RandomAccessFile(dir + newVocabFilename, "rw");
        RandomAccessFile newPost = new RandomAccessFile(dir + (newVocabFilename).replace("vocab", "post"), "rw");

        String lineV1 = voc1.readUTF(), lineV2 = voc2.readUTF();
        while (!lineV1.equals("#end") && !lineV2.equals("#end")) {

            String[] contentsV1 = lineV1.split(" ");
            String[] contentsV2 = lineV2.split(" ");

            String currentTermV1 = contentsV1[0];
            String currentTermV2 = contentsV2[0];

            int comp = currentTermV1.compareTo(currentTermV2);
            if (comp == 0) {
                mergeContentsAndCopyToRAF(contentsV1, contentsV2, post1, post2, newVoc, newPost);
                lineV1 = voc1.readUTF();
                lineV2 = voc2.readUTF();
            } else if (comp < 0) {
                copyContentsToRAF(contentsV1, post1, newVoc, newPost);
                lineV1 = voc1.readUTF();
            } else {
                copyContentsToRAF(contentsV2, post2, newVoc, newPost);
                lineV2 = voc2.readUTF();
            }
        }

        while (!lineV1.equals("#end")) {
            String[] contentsV1 = lineV1.split(" ");
            copyContentsToRAF(contentsV1, post1, newVoc, newPost);
            lineV1 = voc1.readUTF();
        }

        while (!lineV2.equals("#end")) {
            String[] contentsV2 = lineV2.split(" ");
            copyContentsToRAF(contentsV2, post2, newVoc, newPost);
            lineV2 = voc2.readUTF();
        }

        voc1.close();
        post1.close();
        voc2.close();
        post2.close();

        newVoc.writeUTF("#end");
        newPost.writeUTF("#end");
        newVoc.close();
        newPost.close();

        //printVocabRaf(dir + newVocabFilename);
        //printPostingsRaf(dir + (newVocabFilename).replace("vocab", "post"));
        //System.exit(-1);
        vocabFileNames.add(newVocabFilename);
    }

    private void mergePartialFiles(String partialFilesDirectory, ArrayList<String> vocabFileNames, String outputDirectoryPath) throws FileNotFoundException, IOException {
        int n_counter = 0;
        while (vocabFileNames.size() > 1) {
            String vocab1 = partialFilesDirectory + vocabFileNames.remove(0);
            String vocab2 = partialFilesDirectory + vocabFileNames.remove(0);

            mergePartialFiles(vocabFileNames, vocab1, vocab2, partialFilesDirectory, n_counter++);
        }

        String vocabFilename = vocabFileNames.get(0);
        String postFilename = vocabFilename.replace("vocab", "post");

        Files.deleteIfExists(new File(outputDirectoryPath + "vocabulary.txt").toPath());
        Files.deleteIfExists(new File(outputDirectoryPath + "postings.txt").toPath());

        Files.copy(new File(partialFilesDirectory + vocabFilename).toPath(), new File(outputDirectoryPath + "vocabulary.txt").toPath());
        Files.copy(new File(partialFilesDirectory + postFilename).toPath(), new File(outputDirectoryPath + "postings.txt").toPath());

    }

    public BioMedicIndexer() {
        initialize();
    }

    public void loadStopWords(String stopWordsFilePath) throws FileNotFoundException {
        ArrayList<String> fileStopWords = CommonUtilities.getFileContentsByLineUTF_8(stopWordsFilePath);
        stopWords.addAll(fileStopWords);
    }

    public IndexResult indexNXMLDirectory(String directoryBasePath, String outputDirectoryPath) throws IOException {

        ArrayList<PlotPoint> memoryPerDocPlot = new ArrayList<>();
        ArrayList<PlotPoint> timePerDocPlot = new ArrayList<>();

        long startTime = System.nanoTime(), currentTime;

        String documentsFilepath = outputDirectoryPath + "documents.txt";
        String partialFilesDirectory = outputDirectoryPath + "partialIndexing/";

        Collection<String> filepaths = CommonUtilities.getFilesOfDirectory(directoryBasePath);
        ArrayList<String> partialVocabsFilenames = new ArrayList<>();

        int documentCounter = 0;
        int partialCounter = 0;

        RandomAccessFile documentsRAF = new RandomAccessFile(documentsFilepath, "rw");
        documentsRAF.seek(0);

        //Part 1. Produce the Partial Files
        System.out.println(">>BioMedic Indexer started creating the partial files");
        for (String filepath : filepaths) {
            Doc doc = new Doc(documentCounter, filepath);

            doc.setDocFilePointer(documentsRAF.getFilePointer());
            //System.out.println("doc: " + doc.getId());
            docPointerPairs.put(doc.getId(), doc.getDocFilePointer());

            if (vocabulary.size() >= PARTIAL_INDEX_THRESHOLD) {
                createPartialFiles(partialCounter++, partialFilesDirectory, partialVocabsFilenames);
            }

            readNXMLFile(doc);
            documentCounter++;

            if (documentCounter % PARTIAL_INDEX_LOGGING_POINT == 0) {
                double usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000.0;
                double timePassed = (System.nanoTime() - startTime) / 1000000000.0;

                System.out.println(">>Proccessed " + documentCounter + " of " + filepaths.size() + " documents. Used Memory: " + usedMemory + " MBytes. Time passed (seconds): " + timePassed);

                memoryPerDocPlot.add(new PlotPoint(documentCounter, usedMemory));
                timePerDocPlot.add(new PlotPoint(documentCounter, timePassed / 3600));
            }

            //write stuff to file
            String docLine = doc.getId() + " " + doc.getPath() + " " + doc.getNorm() + " \n";
            documentsRAF.writeUTF(docLine);
        }

        createPartialFiles(partialCounter++, partialFilesDirectory, partialVocabsFilenames);

        documentsRAF.writeUTF("#end");
        documentsRAF.close();

        currentTime = System.nanoTime();
        long partitioningTimeElapsed = currentTime - startTime;
        System.out.println(">>BioMedic Indexer created the partial files. Procceeding to merging phase.");

        // Part2. Merging Phase
        long mergingTimeStart = System.nanoTime();
        mergePartialFiles(partialFilesDirectory, partialVocabsFilenames, outputDirectoryPath);
        currentTime = System.nanoTime();
        long mergingTimeElapsed = currentTime - mergingTimeStart;

        // Part3. Produce Vector Norms (in a separate file)
        long vmTimeStart = System.nanoTime();
        VectorModel v = new VectorModel();
        v.initializeDocumentVectorsToFileOptimized(documentsFilepath, outputDirectoryPath + "vocabulary.txt", outputDirectoryPath + "postings.txt", outputDirectoryPath + "vectors.txt", outputDirectoryPath + "mappings.txt", filepaths.size());
        currentTime = System.nanoTime();
        long vmTimeElapsed = currentTime - vmTimeStart;

        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;

        IndexResult ir = new IndexResult(timeElapsed, partitioningTimeElapsed, mergingTimeElapsed, vmTimeElapsed, PARTIAL_INDEX_THRESHOLD, documentCounter, directoryBasePath, outputDirectoryPath);

        // Part4. Create Plots and Log the BioMedicIndexer results
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputDirectoryPath + "log_report.txt"));
        writer.write(ir.toString());
        writer.close();

        PlotGenerator pg = new PlotGenerator();
        pg.generatePRPlot(memoryPerDocPlot, outputDirectoryPath + "memory", "Memory Used by BioMedic Indexer", "Docs", "Memory (MBytes)");
        pg.generatePRPlot(timePerDocPlot, outputDirectoryPath + "time", "Time passed by BioMedic Indexer", "Docs", "Time (hours)");

        //pg.generatePRPlot(memoryPerDocPlot.subList(0, 10), outputDirectoryPath + "memory_sample", "Memory Used by BioMedic Indexer", "Docs", "Memory (MBytes)");
        //pg.generatePRPlot(timePerDocPlot.subList(0, 10), outputDirectoryPath + "time_sample", "Time passed by BioMedic Indexer", "Docs", "Time (seconds)");
        //System.out.println(ir.toString());
        //RafPrinter.printRaf(outputDirectoryPath + "vocabulary.txt");
        //RafPrinter.printRaf(outputDirectoryPath + "documents.txt");
        //RafPrinter.printRaf(outputDirectoryPath + "postings.txt");
        //RafPrinter.printRaf(outputDirectoryPath + "vectors.txt");
        return ir;
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
