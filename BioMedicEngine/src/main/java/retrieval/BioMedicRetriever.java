/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval;

import generalStructures.Doc;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author manos
 */
public class BioMedicRetriever {

    RandomAccessFile documentsRaf;
    RandomAccessFile vocabularyRaf;
    RandomAccessFile postingRaf;

    TreeMap<String, SearchTerm> vocabulary;

    QueryProcessor queryProcessor;
    
    public BioMedicRetriever(String documentsFile, String postingFile, String vocabularyFile) throws FileNotFoundException {
        documentsRaf = new RandomAccessFile(documentsFile, "rw");
        postingRaf = new RandomAccessFile(postingFile, "rw");
        vocabularyRaf = new RandomAccessFile(vocabularyFile, "rw");

        vocabulary = new TreeMap<>();
        queryProcessor = new QueryProcessor();
    }

    public void loadVocabulary() throws IOException {
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
            //System.out.println("Loaded term " + value);
        }
        vocabularyRaf.seek(0);

        System.out.println(">>Total terms loaded: " + vocabulary.size());
    }

    public ArrayList<Doc> findRelevantDocumentsOfDoc(String query){
        ArrayList<Doc>relevantDocs = new ArrayList<>();
        
        ArrayList<String>queryTerms = queryProcessor.parseQuery(query);
        
        for(String cterm : queryTerms){
            if(!vocabulary.containsKey(cterm)){
                continue;
            }
            
            SearchTerm term = vocabulary.get(cterm);
        }
        
        return relevantDocs;
    }
}
