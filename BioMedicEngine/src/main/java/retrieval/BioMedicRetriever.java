/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval;

import commonUtilities.CommonUtilities;
import commonUtilities.RafPrinter;
import generalStructures.SearchTerm;
import generalStructures.Doc;
import generalStructures.DocResult;
import generalStructures.SearchResult;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import mitos.stemmer.Stemmer;

/**
 * BioMedicRetriever contains all the methods needed to perform query answering
 * over a biomedical document collection
 *
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
public class BioMedicRetriever {

    RandomAccessFile documentsRaf;
    RandomAccessFile vocabularyRaf;
    RandomAccessFile postingRaf;
    RandomAccessFile normsRaf;

    TreeMap<Integer, Long> docNormsPairs;
    TreeMap<String, SearchTerm> vocabulary;

    int totalDocuments;
    int totalTerms;

    QueryProcessor queryProcessor;

    private String findSnippet(Doc d, String query) throws IOException {
        String rawC = CommonUtilities.getRawNXMLFileContent(d.getPath());

        char[] rawCarr = rawC.toCharArray();

        int pos = CommonUtilities.KnuthMorrisPrattSearch(query.toCharArray(), rawCarr);

        if (pos < 0) {
            return "empty";
        }

        int start = pos, end = pos + query.toCharArray().length;
        int words2goBack = 1;
        int wordsPassed = 0;
        int words2goForth = 1;
        while (pos > 0) {
            start = pos;
            if (rawCarr[pos] == ' ') {
                wordsPassed++;
            }
            pos--;

            if (wordsPassed == words2goBack) {
                break;
            }

        }

        wordsPassed = 0;
        while (pos < rawCarr.length) {
            end = pos;
            if (rawCarr[pos] == ' ') {
                wordsPassed++;
            }
            pos++;

            if (wordsPassed == words2goForth) {
                break;
            }

        }

        return rawC.substring(start, end);

    }

    private double getDocumentTFOfTerm(Doc d, String str) throws IOException {
        double tf = 0;

        SearchTerm t = vocabulary.get(str); //checked if contained before calling this method!

        //seek the postings of term to see if docID is present!
        postingRaf.seek(t.getFp());
        String line;
        while ((line = postingRaf.readUTF()) != null) {
            if (line.equals("#end") || line.equals("#stop\n")) {
                break;
            }

            String[] contents = line.split(" ");
            int currDocID = Integer.parseInt(contents[0]);
            double currTF = Double.parseDouble(contents[1]);
            if (currDocID == d.getId()) {
                tf = currTF;
                break;
            } else if (currDocID > d.getId()) {
                break;
            }
        }

        return tf;
    }

    private double calculateScore(Doc d, TreeMap<String, Double> queryTermsTF, double queryNorm, TreeMap<String, HashMap<Doc, Double>> termTFinDocs) throws IOException {
        //1. traverse treemap and find the dot_p of every matching terms:
        double dotProduct = 0;
        for (Map.Entry<String, Double> entry : queryTermsTF.entrySet()) {
            double qTF = entry.getValue();
            String termVal = entry.getKey();
            if (vocabulary.containsKey(termVal)) {
                SearchTerm c = vocabulary.get(termVal);

                double iDF = CommonUtilities.getIDF(c.getDf(), totalDocuments);
                double dTF = 0;//getDocumentTFOfTerm(d, termVal);
                //System.out.println("DTF = " + dTF);
                if (termTFinDocs.containsKey(termVal)) {
                    //System.out.println("Contains key " + termVal);
                    HashMap<Doc, Double> map = termTFinDocs.get(termVal);
                    if (map.containsKey(d)) {
                        dTF = map.get(d);
                        //System.out.println("TF of Key " + termVal + " is " + dTF);
                    }
                }

                double doc_i = dTF * iDF;
                double query_i = qTF * iDF;

                dotProduct += (doc_i * query_i);
            }
        }

        //2. get norm of doc d
        long pos = docNormsPairs.get(d.getId());
        normsRaf.seek(pos);
        String l = normsRaf.readUTF(); //no good but I guess its fine?

        double norm = Double.parseDouble((l.split(" "))[1]);
        return dotProduct / (norm * queryNorm);
    }

    private void initializeDocNormsPairs(String filename) throws FileNotFoundException, IOException {
        docNormsPairs = new TreeMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] conts = line.split(" ");
                docNormsPairs.put(Integer.parseInt(conts[0]), Long.parseLong(conts[1]));
            }
        }

        totalDocuments = docNormsPairs.size();
    }

    private HashMap<Doc, Double> traversePostingsOfTerm(ArrayList<Doc> relevantDocs, SearchTerm term) throws IOException {
        postingRaf.seek(term.getFp());
        String line;

        HashMap<Doc, Double> termTFofDoc = new HashMap<>();

        while ((line = postingRaf.readUTF()) != null) { //this is just a dummy != to read the value inside while.
            if (line.equals("#stop\n") || line.equals("#end")) {
                break;
            }

            //System.out.println(line);
            String[] contents = line.split(" ");

            if (contents[3] == null || contents[3].equals("null")) {
                continue;
            }

            double tfOfDoc = Double.parseDouble(contents[1]);
            long docPointer = Long.parseLong(contents[3]);

            documentsRaf.seek(docPointer);
            String docInfo = documentsRaf.readUTF();
            String[] docContents = docInfo.split(" ");
            int id = Integer.parseInt(docContents[0]);
            String path = docContents[1];
            Doc doc = new Doc(id, path);

            termTFofDoc.put(doc, tfOfDoc);

            //System.out.println("haha");
            if (!relevantDocs.contains(doc)) {
                relevantDocs.add(doc);
            }
        }

        return termTFofDoc;
    }

    public void printReportLogDEBUG(String path) throws IOException {
        FileWriter fw = new FileWriter(path + "report_debug_vocab.txt");

        vocabularyRaf.seek(0);
        //3 027 032
        int limit = 3000000;

        String line;
        int counter = 0;
        int docCounter = 0;

        for (Map.Entry<String, SearchTerm> v : vocabulary.entrySet()) {
            docCounter = 0;
            SearchTerm t = v.getValue();

            String val = t.getValue();
            long ptr = t.getFp();
            int df = t.getDf();

            fw.write("\n\n=======" + val + " " + df + " " + ptr + "=======\n");

            postingRaf.seek(ptr);
            String postingLine;
            while ((postingLine = postingRaf.readUTF()) != null) { //this is just a dummy != to read the value inside while.
                if (postingLine.equals("#stop\n") || postingLine.equals("#end")) { //maybe this could be a problem when concataiting
                    break;
                }
                docCounter++;

                String[] postingContents = postingLine.split(" ");

                String docID = postingContents[0];
                String tf = postingContents[1];
                String docPos = postingContents[3];
                fw.write(docCounter + " " + docID + " " + tf + " " + docPos + "\n");

            }
            counter++;
            if (counter > limit) {
                break;
            }
        }
        fw.close();
    }

    private double findQueryNorm(TreeMap<String, Double> queryTermsTF) {
        double norm = 0;

        for (Map.Entry<String, Double> entry : queryTermsTF.entrySet()) {
            double queryTF = entry.getValue();
            String queryTerm = entry.getKey();
            if (vocabulary.containsKey(queryTerm)) {
                SearchTerm t = vocabulary.get(queryTerm);
                int df = t.getDf();
                //System.out.println("td = " + totalDocuments);
                double iDF = CommonUtilities.getIDF(df, totalDocuments);
                //System.out.println("df = " + df + " idf = " + iDF);
                norm += (iDF * queryTF) * (iDF * queryTF); //na checkarw oti einai swsto auto OK!
            }
        }

        return Math.sqrt(norm);
    }

    public BioMedicRetriever(String documentsFile, String postingFile, String vocabularyFile, String normsFile, String mappingsFile) throws FileNotFoundException, IOException {
        documentsRaf = new RandomAccessFile(documentsFile, "rw");
        postingRaf = new RandomAccessFile(postingFile, "rw");
        vocabularyRaf = new RandomAccessFile(vocabularyFile, "rw");
        normsRaf = new RandomAccessFile(normsFile, "rw");

        queryProcessor = new QueryProcessor();

        //RafPrinter.printRaf(vocabularyFile);
        initializeDocNormsPairs(mappingsFile);
        loadVocabulary();

    }

    public void addStopWords(String path) throws FileNotFoundException {
        queryProcessor.loadStopWords(path);
    }

    public void loadVocabulary() throws IOException {
        vocabulary = new TreeMap<>();
        vocabularyRaf.seek(0);
        int counter = 0;
        String line;
        while ((line = vocabularyRaf.readUTF()) != null) {
            if (line.equals("#end")) {
                break;
            }

            String[] contents = line.split(" ");
            String value = contents[0];
            int df = Integer.parseInt(contents[1]);
            long ptr = Long.parseLong(contents[2]);

            if (vocabulary.containsKey(value)) {
                System.out.println("Something is wrong in the vocab file. Found duplicate.");
                System.out.println(value);
                System.exit(-1);
            }

            vocabulary.put(value, new SearchTerm(value, df, ptr));
            if (counter < 100) {
                //System.out.println("Loaded term " + value + " with df " + df);
            }
            counter++;
        }
        vocabularyRaf.seek(0);
        //System.out.println(vocabulary);
        System.out.println(">>Total terms loaded: " + vocabulary.size());
        totalTerms = vocabulary.size();
    }

    public ArrayList<Doc> findRelevantDocumentsOfQuery(ArrayList<String> queryTerms, TreeMap<String, HashMap<Doc, Double>> termTFinDocs) throws IOException {
        ArrayList<Doc> relevantDocs = new ArrayList<>();

        for (String cterm : queryTerms) {
            if (!vocabulary.containsKey(cterm)) {
                continue;
            }

            SearchTerm term = vocabulary.get(cterm);
            HashMap<Doc, Double> res = traversePostingsOfTerm(relevantDocs, term);

            termTFinDocs.put(cterm, res);
        }

        return relevantDocs;
    }

    public SearchResult findRelevantDocumentsOfQuery(String query) throws IOException {
        long startTime = System.nanoTime();
        ArrayList<DocResult> results = new ArrayList<>();
        TreeMap<String, Double> queryTermsTF = queryProcessor.parseQueryFindTF(query);

        //System.out.println("Query Terms = " + queryTermsTF.size());
        //System.out.println(queryTermsTF);
        TreeMap<String, HashMap<Doc, Double>> termTFinDocs = new TreeMap<>();
        ArrayList<Doc> relevantDocuments = findRelevantDocumentsOfQuery(new ArrayList<>(queryTermsTF.keySet()), termTFinDocs);
        //System.out.println("Total relevant documents " + relevantDocuments.size());
        double queryNorm = findQueryNorm(queryTermsTF);

        for (Doc d : relevantDocuments) {
            double score = calculateScore(d, queryTermsTF, queryNorm, termTFinDocs);
            //String snippet = findSnippet(d, query);
            results.add(new DocResult(d, score, ""));
        }

        Collections.sort(results, new Comparator<DocResult>() {
            public int compare(DocResult o1, DocResult o2) {
                return -1 * Double.compare(o1.getScore(), o2.getScore());
            }
        });
        long endTime = System.nanoTime();
        long responseTime = endTime - startTime;

        return new SearchResult(results, responseTime);

    }

    public SearchResult findRelevantTopic(String query, String topic, double topicWeight, boolean intersection) throws IOException {

        double normalWeight = 1 - topicWeight;

        long startTime = System.nanoTime();
        TreeMap<String, HashMap<Doc, Double>> termTFinDocs = new TreeMap<>();
        TreeMap<String, HashMap<Doc, Double>> termTFinDocsTopic = new TreeMap<>();

        ArrayList<DocResult> rawResults = new ArrayList<>();

        TreeMap<String, Double> queryTermsTF = queryProcessor.parseQueryFindTF(query);
        ArrayList<Doc> relevantDocuments = findRelevantDocumentsOfQuery(new ArrayList<>(queryTermsTF.keySet()), termTFinDocs);

        TreeMap<String, Double> topicTermsTF = queryProcessor.parseQueryFindTF(topic);
        ArrayList<Doc> topicDocuments = findRelevantDocumentsOfQuery(new ArrayList<>(topicTermsTF.keySet()), termTFinDocsTopic);

        double queryNorm = findQueryNorm(queryTermsTF);
        double topicNorm = findQueryNorm(topicTermsTF);

        //relevantDocuments = new ArrayList<>(relevantDocuments.stream().distinct().collect(Collectors.toList()));
        //System.out.println("ResSize=" + relevantDocuments.size() + " TopSize=" + topicDocuments.size());
        //System.out.println(topicTermsTF);
        //System.out.println(termTFinDocsTopic);
        if (intersection) {
            relevantDocuments.retainAll(topicDocuments);
        }
        //System.out.println(relevantDocuments.size());
        for (Doc d : relevantDocuments) {
            double score = calculateScore(d, queryTermsTF, queryNorm, termTFinDocs);
            double topicScore = calculateScore(d, topicTermsTF, topicNorm, termTFinDocsTopic);
            //System.out.println(topicScore);
            double finalScore = normalWeight * score + topicWeight * topicScore;

            String snippet = "";
            rawResults.add(new DocResult(d, finalScore, snippet));
        }

        Collections.sort(rawResults, new Comparator<DocResult>() {
            public int compare(DocResult o1, DocResult o2) {
                return -1 * Double.compare(o1.getScore(), o2.getScore());
            }
        });

        long endTime = System.nanoTime();
        long responseTime = endTime - startTime;
        return new SearchResult(rawResults, responseTime);
    }

}
