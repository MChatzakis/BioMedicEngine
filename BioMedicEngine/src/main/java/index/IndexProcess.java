/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

/**
 *
 * @author manos
 */
public class IndexProcess {

    public static void main(String[] args) throws Exception {
        //B1
        BioMedicIndexer index = new BioMedicIndexer();
        

        String englishStopWordsFile = "./stopwords/stopwordsEn.txt";
        index.loadStopWords(englishStopWordsFile);

        String greekStopWordsFile = "./stopwords/stopwordsGr.txt";
        index.loadStopWords(greekStopWordsFile);

        System.out.println("Total stopwords: " + index.getStopWords().size());
        System.out.println("Stop words of the indexer are: " + index.getStopWords());
        
        String singleNXMLfile = "./sample/MiniCollection/MiniCollection/treatment/Topic_27/0/1936313.nxml";
        index.readNXMLFile(singleNXMLfile);
    }
}
