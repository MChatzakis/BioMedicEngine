/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class Term {

    private int df;
    private String value;

    private TreeMap<Integer, HashMap<String, Integer>> perDocumentTagOccurences;

    private void initializeTagOccurencesOfDocument(String[] tags, int docID) {
        HashMap<String, Integer> tagOccurences = perDocumentTagOccurences.get(docID);
        for (String tag : tags) {
            tagOccurences.put(tag, 0);
        }
    }

    private void increaseOccurenceOfWordInTag(String tag, int docID) {
        HashMap<String, Integer> tagOccurences = perDocumentTagOccurences.get(docID);
        if (tagOccurences.containsKey(tag)) {
            tagOccurences.put(tag, tagOccurences.get(tag) + 1);
        } else {
            System.out.println("Something went wrong with tag initialization of term " + value);
            System.exit(-1);
        }

    }

    public Term(String value) {
        this.value = value;

        df = 0;
        perDocumentTagOccurences = new TreeMap<>();
    }

    public void addOccurence(String[] tags, String tag, int docID) {
        if (!perDocumentTagOccurences.containsKey(docID)) {
            df++;
            perDocumentTagOccurences.put(docID, new HashMap<>());
            initializeTagOccurencesOfDocument(tags, docID);
        }

        increaseOccurenceOfWordInTag(tag, docID);
    }

    public String toString() {
        //String s = "{" + value + "} => TO" + tagOccurences.toString() + " - DIDs" + documentOccurences.toString() + " - DF: " + df;
        String s;

        s = "{" + value + "} => DF: " + df + " => " + perDocumentTagOccurences.toString();

        return s;
    }

}
