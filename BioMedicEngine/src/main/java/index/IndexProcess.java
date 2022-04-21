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
        
        //B2 & B3, stemming is also performed here
        index.processNXMLDirectory("C://MedicalCollection");
    }
}
