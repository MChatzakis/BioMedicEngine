/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import index.BioMedicIndexer;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        String documentsFile = "./collectionIndex/documentsFile.txt";
        String postingFile = "./collectionIndex/postings.txt";

        BioMedicRetriever bmr = new BioMedicRetriever(documentsFile, postingFile, vocabularyFile);

        bmr.loadVocabulary();
    }

    public static void main(String[] args) throws Exception {
        Stemmer.Initialize();
        
        //createIndex();
        queryAnswering();
    }
}
