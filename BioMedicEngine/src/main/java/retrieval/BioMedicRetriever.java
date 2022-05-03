/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval;

import commonUtilities.CommonUtilities;
import generalStructures.SearchTerm;
import generalStructures.Doc;
import generalStructures.DocResult;
import generalStructures.SearchResult;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author manos
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

    private double getDocumentTFOfTerm(Doc d, String str) throws IOException {
        double tf = 0;

        SearchTerm t = vocabulary.get(str); //checked if contained before calling this method!

        //seek the postings of term to see if docID is present!
        postingRaf.seek(t.getFp());
        String line;
        while ((line = postingRaf.readUTF()) != null) {
            if (line.equals("#end") || line.equals("#stop \n")) {
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

    private double calculateScore(Doc d, TreeMap<String, Double> queryTermsTF, double queryNorm) throws IOException {
        //1. traverse treemap and find the dot_p of every matching terms:
        double dotProduct = 0;
        for (Map.Entry<String, Double> entry : queryTermsTF.entrySet()) {
            double qTF = entry.getValue();
            String termVal = entry.getKey();
            if (vocabulary.containsKey(termVal)) {
                SearchTerm c = vocabulary.get(termVal);
                double iDF = CommonUtilities.getIDF(c.getDf(), totalDocuments);

                double dTF = getDocumentTFOfTerm(d, termVal);

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
        //double norm = Math.sqrt(Double.parseDouble((l.split(" "))[1]));

        return dotProduct / (norm * queryNorm); //cosSIm
    }

    private void initializeDocNormsPairs(String filename) throws FileNotFoundException, IOException {
        docNormsPairs = new TreeMap<>();
        try ( BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] conts = line.split(" ");
                docNormsPairs.put(Integer.parseInt(conts[0]), Long.parseLong(conts[1]));
            }
        }

        totalDocuments = docNormsPairs.size();
    }

    private void traversePostingsOfTerm(ArrayList<Doc> relevantDocs, SearchTerm term) throws IOException {
        postingRaf.seek(term.getFp());
        String line;

        while ((line = postingRaf.readUTF()) != null) { //this is just a dummy != to read the value inside while.
            if (line.equals("#stop\n") || line.equals("#end")) {
                break;
            }

            String[] contents = line.split(" ");
            long docPointer = Long.parseLong(contents[3]);

            documentsRaf.seek(docPointer);
            String docInfo = documentsRaf.readUTF();
            String[] docContents = docInfo.split(" ");
            int id = Integer.parseInt(docContents[0]);
            String path = docContents[1];
            Doc doc = new Doc(id, path);

            if (!relevantDocs.contains(doc)) {
                relevantDocs.add(doc);
            }
        }
    }

    private double findQueryNorm(TreeMap<String, Double> queryTermsTF) {
        double norm = 0;

        for (Map.Entry<String, Double> entry : queryTermsTF.entrySet()) {
            double queryTF = entry.getValue();
            String queryTerm = entry.getKey();
            if (vocabulary.containsKey(queryTerm)) {
                SearchTerm t = vocabulary.get(queryTerm);
                int df = t.getDf();
                double iDF = CommonUtilities.getIDF(df, totalDocuments);

                norm += (iDF * queryTF) * (iDF * queryTF); //na checkarw oti einai swsto auto
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

        initializeDocNormsPairs(mappingsFile);
        loadVocabulary();
    }

    public void loadVocabulary() throws IOException {
        vocabulary = new TreeMap<>();
        vocabularyRaf.seek(0);

        String line;
        while ((line = vocabularyRaf.readUTF()) != null) {
            if (line.equals("#end")) {
                break;
            }

            String[] contents = line.split(" ");
            String value = contents[0];
            int df = Integer.parseInt(contents[1]);
            long ptr = Long.parseLong(contents[2]);

            vocabulary.put(value, new SearchTerm(value, df, ptr));
            System.out.println("Loaded term " + value);
        }
        vocabularyRaf.seek(0);

        System.out.println(">>Total terms loaded: " + vocabulary.size());
        totalTerms = vocabulary.size();
    }

    public ArrayList<Doc> findRelevantDocumentsOfQuery(ArrayList<String> queryTerms) throws IOException {
        ArrayList<Doc> relevantDocs = new ArrayList<>();

        //ArrayList<String> queryTerms = queryProcessor.parseQuery(query);
        for (String cterm : queryTerms) {
            if (!vocabulary.containsKey(cterm)) {
                continue;
            }

            SearchTerm term = vocabulary.get(cterm);
            traversePostingsOfTerm(relevantDocs, term);
        }

        return relevantDocs;
    }

    public SearchResult findRelevantDocumentsOfQuery(String query) throws IOException {
        TreeMap<Double, DocResult> results = new TreeMap<>(Collections.reverseOrder());
        TreeMap<String, Double> queryTermsTF = queryProcessor.parseQueryFindTF(query);

        ArrayList<Doc> relevantDocuments = findRelevantDocumentsOfQuery(new ArrayList<>(queryTermsTF.keySet()));

        double queryNorm = findQueryNorm(queryTermsTF);

        for (Doc d : relevantDocuments) {
            double score = calculateScore(d, queryTermsTF, queryNorm);
            results.put(score, new DocResult(d, score));
        }

        return new SearchResult(results, 0.0);

    }

}
