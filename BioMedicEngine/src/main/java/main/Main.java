/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import index.BioMedicIndexer;
import java.io.IOException;

/**
 *
 * @author manos
 */
public class Main {

    public static void createIndex() throws IOException{
        BioMedicIndexer index = new BioMedicIndexer();
        
        String englishStopWordsFile = "./stopwords/stopwordsEn.txt";
        index.loadStopWords(englishStopWordsFile);

        String greekStopWordsFile = "./stopwords/stopwordsGr.txt";
        index.loadStopWords(greekStopWordsFile);

        //System.out.println("Total stopwords: " + index.getStopWords().size());
        //System.out.println("Stop words of the indexer are: " + index.getStopWords());
        
        index.processNXMLDirectory("C://MedicalCollection");
    }
    
    public static void main(String[] args) throws Exception {
       
        createIndex();
    }
}
