package retrieval;

import commonUtilities.CommonUtilities;
import generalStructures.Doc;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import mitos.stemmer.Stemmer;

/**
 * Query Processor contains the query parsing methods.
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

    public TreeMap<String, Double> parseQueryFindTF(String query) {
        TreeMap<String, Double> termsTF = new TreeMap<>();

        String delimiter = "\t\n\r\f ";
        StringTokenizer tokenizer = new StringTokenizer(query, delimiter);
        while (tokenizer.hasMoreTokens()) {
            String currentWord = removeStopPointsFromText(tokenizer.nextToken());

            if (stopWords.contains(currentWord) || currentWord.equals("")) {
                continue;
            }

            String stemmedTerm = Stemmer.Stem(currentWord);

            if (!termsTF.containsKey(stemmedTerm)) {
                termsTF.put(stemmedTerm, 1.0);
            } else {
                termsTF.put(stemmedTerm, termsTF.get(stemmedTerm) + 1.0);
            }
        }

        //find TF
        double maxF = 0;
        for (Map.Entry<String, Double> entry : termsTF.entrySet()) {
            if (entry.getValue() > maxF) {
                maxF = entry.getValue();
            }
        }

        for (Map.Entry<String, Double> entry : termsTF.entrySet()) {
            termsTF.put(entry.getKey(), entry.getValue() / maxF);
        }

        return termsTF;
    }

}
