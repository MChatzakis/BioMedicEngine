package cmdApp;

import commonUtilities.CommonUtilities;
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
        options.addOption("gr", true, "BioMedic Indexer greek stopwords");
        options.addOption("en", true, "BioMedic Indexer english stopwords");

        options.addOption("collection", true, "BioMedic Query answering collection directory");

        CommandLine cmd = parser.parse(options, args);
        //java -jar BioMedicEngine-1.0-SNAPSHOT-exejar.jar -mode indexer -input C:\Users\manos\Documents\GitHub\BioMedicEngine\BioMedicEngine\sample\ -output C:\Users\manos\Desktop\simple_example\ -gr C:\Users\manos\Documents\GitHub\BioMedicEngine\BioMedicEngine\stopwords\stopwordsGr.txt -en C:\Users\manos\Documents\GitHub\BioMedicEngine\BioMedicEngine\stopwords\stopwordsEn.txt
        if (cmd.hasOption("mode")) {
            String mode = cmd.getOptionValue("mode");

            switch (mode) {
            case "indexer":
                if (cmd.hasOption("input") && cmd.hasOption("output") && cmd.hasOption("gr")
                        && cmd.hasOption("en")) {
                    String input = cmd.getOptionValue("input");
                    String output = cmd.getOptionValue("output");
                    String gr = cmd.getOptionValue("gr");
                    String en = cmd.getOptionValue("en");

                    createIndex(en, gr, output, input);
                } else {
                    System.out.println("BioMedic Engine requires all indexing arguments. Exiting...");
                    System.exit(-1);
                }
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

    public static void experimentQueries(String basePath) throws IOException {
        ArrayList<String> queries = CommonUtilities.readXML("corpus/topics.xml", "topic", "summary");
        ArrayList<String> types = CommonUtilities.readXMLattr("corpus/topics.xml", "topic", "type");

        System.out.println(queries);
        System.out.println(types);

        String vocabularyFile = basePath + "vocabulary.txt";
        String documentsFile = basePath + "documents.txt";
        String postingFile = basePath + "postings.txt";
        String normsFile = basePath + "vectors.txt";
        String mappingsFile = basePath + "mappings.txt";

        ArrayList<Double> times = new ArrayList<>();

        BioMedicRetriever bmr = new BioMedicRetriever(documentsFile, postingFile, vocabularyFile, normsFile, mappingsFile);

        for (int i = 0; i < queries.size(); i++) {
            String q = queries.get(i);
            String t = types.get(i);
            SearchResult results = bmr.findRelevantTopic(q, t);
            //System.out.println("Response Time: " + results.getResponseTime() / 1000000000.0);

            times.add(results.getResponseTime() / 1000000000.0);
        }

        System.out.println(times);
    }

    public static void main(String[] args) throws Exception {

        Stemmer.Initialize();

        //System.out.println("Hi!");
        //bioMedicCLI(args);
        //createIndex("./stopwords/stopwordsEn.txt", "./stopwords/stopwordsGr.txt", "./collectionIndex/", "C://MedicalCollection/");
        //queryAnsweringSimple("C://Users/manos/Desktop/simple_example/");
        //queryAnsweringSimple("C:/Users/manos/Desktop/collectionIndex/");
        //queryAnsweringTopics("C://Users/manos/Desktop/simple_example/");
        
        experimentQueries("C:/Users/manos/Desktop/collectionIndex/");
    }
}
