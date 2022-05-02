/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmdApp;

import generalStructures.Doc;
import index.BioMedicIndexer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
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

        BioMedicRetriever bmr = new BioMedicRetriever(documentsFile, postingFile, vocabularyFile);

        bmr.loadVocabulary();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println(">>> Type a query");
            String inputQuery = sc.nextLine();

            if (inputQuery.equals("!exit")) {
                break;
            }

            ArrayList<Doc> results = bmr.findRelevantDocumentsOfDoc(inputQuery);
            System.out.println("Results of query " + inputQuery);
            for (Doc d : results) {
                System.out.println("Document " + d.toString());
            }

        }

    }

    public static void main(String[] args) throws Exception {
        Stemmer.Initialize();

        createIndex();
        //queryAnswering();
    }
}
