package cmdApp;

import generalStructures.Doc;
import generalStructures.DocResult;
import generalStructures.SearchResult;
import index.BioMedicIndexer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import mitos.stemmer.Stemmer;
import retrieval.BioMedicRetriever;

/**
 *
 * @author manos
 */
public class Main {

    public static void createIndex() throws IOException {
        BioMedicIndexer index = new BioMedicIndexer();

        String englishStopWordsFile = "./stopwords/stopwordsEn.txt";
        index.loadStopWords(englishStopWordsFile);

        String greekStopWordsFile = "./stopwords/stopwordsGr.txt";
        index.loadStopWords(greekStopWordsFile);

        String smallCollection = "./sample/";
        String bigCollection = "C://MedicalCollection/";
        index.indexNXMLDirectory(bigCollection, "./collectionIndex/");
    }

    public static void queryAnswering() throws FileNotFoundException, IOException {
        String vocabularyFile = "./collectionIndex/vocabulary.txt";
        String documentsFile = "./collectionIndex/documents.txt";
        String postingFile = "./collectionIndex/postings.txt";

        String normsFile = "./collectionIndex/postings.txt";
        String mappingsFile = "./collectionIndex/postings.txt";

        BioMedicRetriever bmr = new BioMedicRetriever(documentsFile, postingFile, vocabularyFile, normsFile, mappingsFile);

        bmr.loadVocabulary();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println(">>> Type a query");
            String inputQuery = sc.nextLine();

            if (inputQuery.equals("!exit")) {
                break;
            }

            SearchResult results = bmr.findRelevantDocumentsOfQuery(inputQuery);
            for (Map.Entry<Double, DocResult> entry : results.getRelevantDocuments().entrySet()) {
                double score = entry.getKey();
                DocResult docResult = entry.getValue();
                System.out.println(docResult.toString());
            }
            System.out.println("Response Time: " + results.getResponseTime());
        }

    }

    public static void main(String[] args) throws Exception {
        Stemmer.Initialize();

        createIndex();
        //queryAnswering();
    }
}
