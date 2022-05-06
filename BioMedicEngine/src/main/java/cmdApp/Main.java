package cmdApp;

import generalStructures.Doc;
import generalStructures.DocResult;
import generalStructures.IndexResult;
import generalStructures.SearchResult;
import index.BioMedicIndexer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import mitos.stemmer.Stemmer;
import org.apache.commons.cli.CommandLineParser;
import retrieval.BioMedicRetriever;

/**
 *
 * @author manos
 */
public class Main {

    
    public static void bioMedicCLI(String args){
        CommandLineParser parser = new DefaultParser();
    }
    
    public static void createIndex(String englishStopWordsFile, String greekStopWordsFile, String outDir, String bigCollection) throws IOException {
        BioMedicIndexer index = new BioMedicIndexer();

        index.loadStopWords(englishStopWordsFile);
        index.loadStopWords(greekStopWordsFile);

        IndexResult ir = index.indexNXMLDirectory(bigCollection, outDir);
        System.out.println(ir.toString());
    }

    public static void queryAnsweringSimple(String basePath) throws FileNotFoundException, IOException {
        String vocabularyFile = basePath + "vocabulary.txt";
        String documentsFile = basePath + "documents.txt";
        String postingFile = basePath + "postings.txt";
        String normsFile = basePath + "vectors.txt";
        String mappingsFile = basePath + "mappings.txt";

        BioMedicRetriever bmr = new BioMedicRetriever(documentsFile, postingFile, vocabularyFile, normsFile, mappingsFile);

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println(">>> Type a query");
            String inputQuery = sc.nextLine();

            if (inputQuery.equals("!exit")) {
                break;
            }

            SearchResult results = bmr.findRelevantDocumentsOfQuery(inputQuery);
            for (DocResult dr : results.getRelevantDocuments()) {
                System.out.println(dr.toString());
            }

            System.out.println("Response Time: " + results.getResponseTime());
        }

    }

    public static void queryAnsweringTopics(String basePath) throws IOException {
        String vocabularyFile = basePath + "vocabulary.txt";
        String documentsFile = basePath + "documents.txt";
        String postingFile = basePath + "postings.txt";
        String normsFile = basePath + "vectors.txt";
        String mappingsFile = basePath + "mappings.txt";

        BioMedicRetriever bmr = new BioMedicRetriever(documentsFile, postingFile, vocabularyFile, normsFile, mappingsFile);

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println(">>> Type a query");
            String inputQuery = sc.nextLine();
            if (inputQuery.equals("!exit")) {
                break;
            }

            System.out.println(">>> Type an medical type (diagnosis, test, treatment)");
            String type = sc.nextLine();

            SearchResult results = bmr.findRelevantTopic(inputQuery, type);
            for (DocResult dr : results.getRelevantDocuments()) {
                System.out.println(dr.toString());
            }

            System.out.println("Response Time: " + results.getResponseTime());
        }
    }

    public static void main(String[] args) throws Exception {

        Stemmer.Initialize();

        //createIndex("./stopwords/stopwordsEn.txt", "./stopwords/stopwordsGr.txt", "./collectionIndex/", "C://MedicalCollection/");
        //queryAnsweringSimple(/*"C://Users/manos/Desktop/tmpDir/"*/"./collectionIndex/");
        queryAnsweringTopics("./collectionIndex/");

    }
}
