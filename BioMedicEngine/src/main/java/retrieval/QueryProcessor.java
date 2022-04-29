/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval;

import commonUtilities.CommonUtilities;
import generalStructures.Doc;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import mitos.stemmer.Stemmer;

/**
 *
 * @author Manos Chatzakis
 */
public class QueryProcessor {

    private ArrayList<String> stopWords;
    private final String[] stopPoints = {".", ",", "(", ")", "[", "]", "\'", "\"", ";", ":", "?", "*", "&", "#", "@", "-", "!", "~", "<", ">", "{", "}", "=", "|", "\\", "/", "%", "$", "+"};

    private String removeStopPointsFromText(String text) {
        String formattedText = text;
        for (String point : stopPoints) {
            if (CommonUtilities.isNumeric(formattedText) && (point.equals(".") || point.equals(","))) {
                continue;
            }
            formattedText = formattedText.replace(point, "");
        }

        return formattedText;
    }

    public QueryProcessor() {
        stopWords = new ArrayList<>();
        stopWords.addAll(Arrays.asList(stopPoints));
    }

    public void loadStopWords(String stopWordsFilePath) throws FileNotFoundException {
        ArrayList<String> fileStopWords = CommonUtilities.getFileContentsByLineUTF_8(stopWordsFilePath);
        stopWords.addAll(fileStopWords);
    }

    public ArrayList<String> parseQuery(String query) {
        ArrayList<String> queryTerms = new ArrayList<>();

        String delimiter = "\t\n\r\f ";
        StringTokenizer tokenizer = new StringTokenizer(query, delimiter);
        while (tokenizer.hasMoreTokens()) {
            String currentWord = removeStopPointsFromText(tokenizer.nextToken());

            if (stopWords.contains(currentWord) || currentWord.equals("")) {
                continue;
            }

            queryTerms.add(Stemmer.Stem(currentWord));
        }

        return queryTerms;
    }

    
    
}
