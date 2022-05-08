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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import retrieval.BioMedicRetriever;

/**
 *
 * @author manos
 */
public class Main {

    public static void bioMedicCLI(String[] args) throws ParseException, IOException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        options.addOption("mode", true, "BioMedic Engine Mode (indexer, retriever, topicRetriver)");

        options.addOption("input", true, "BioMedic Indexer input directory");
        options.addOption("output", true, "BioMedic Indexer output directory");
        options.addOption("grStopwords", true, "BioMedic Indexer greek stopwords");
        options.addOption("enStopwords", true, "BioMedic Indexer english stopwords");

        options.addOption("collection", true, "BioMedic Query answering collection directory");

        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("mode")) {
            String mode = cmd.getOptionValue("mode");

            switch (mode) {
            case "indexer":
                
                break;
            case "retriever":
                if (cmd.hasOption("collection")) {
                    String collectionPath = cmd.getOptionValue("collection");
                    queryAnsweringSimple(collectionPath);
                } else {
                    System.out.println("BioMedic Engine requires a directory to load the index.");
                    System.exit(-1);
                }

                break;
            case "topicRetriever":
                if (cmd.hasOption("collection")) {
                    String collectionPath = cmd.getOptionValue("collection");
                    queryAnsweringTopics(collectionPath);
                } else {
                    System.out.println("BioMedic Engine requires a directory to load the index.");
                    System.exit(-1);
                }
                break;
            default:
                System.out.println("BioMedic Engine does not support mode with name " + mode + "(modes: indexer, retriever, topicRetriever), exiting... ");

            }
        } else {
            System.out.println("Wrong mode argument, exiting...");
            System.exit(-1);
        }

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

            System.out.println("Response Time: " + results.getResponseTime() / 1000000000.0);
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

            System.out.println(">>> Type a medical type (diagnosis, test, treatment)");
            String type = sc.nextLine();

            SearchResult results = bmr.findRelevantTopic(inputQuery, type);
            for (DocResult dr : results.getRelevantDocuments()) {
                System.out.println(dr.toString());
            }

            System.out.println("Response Time: " + results.getResponseTime() / 1000000000.0);
        }
    }

    public static void main(String[] args) throws Exception {

        Stemmer.Initialize();

        createIndex("./stopwords/stopwordsEn.txt", "./stopwords/stopwordsGr.txt", "./collectionIndex/", "C://MedicalCollection/");
        //queryAnsweringSimple(/*"C://Users/manos/Desktop/tmpDir/"*/"./collectionIndex/");
        //queryAnsweringTopics("./collectionIndex/");

    }
}
