/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class IndexTerm {

    private int df;
    private String value;

    private TreeMap<Integer, HashMap<String, Integer>> perDocumentTagOccurences;
    private TreeMap<Integer, HashMap<String, ArrayList<Integer>>> perDocumentTagPositions;
    private TreeMap<Integer, Double> perDocumentTF;

    private void initializeTagOccurencesOfDocument(String[] tags, int docID) {
        HashMap<String, Integer> tagOccurences = perDocumentTagOccurences.get(docID);
        for (String tag : tags) {
            tagOccurences.put(tag, 0);
        }
    }

    private void initializeTagPositionsOfDocument(String[] tags, int docID) {
        HashMap<String, ArrayList<Integer>> tagOccurences = perDocumentTagPositions.get(docID);
        for (String tag : tags) {
            tagOccurences.put(tag, new ArrayList<>());
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

    private void increasePositionOfWordInTag(String tag, int docID, int positionInTag) {
        HashMap<String, ArrayList<Integer>> tagOccurences = perDocumentTagPositions.get(docID);
        if (tagOccurences.containsKey(tag)) {
            ArrayList<Integer> positions = tagOccurences.get(tag);
            positions.add(positionInTag);
            //tagOccurences.put(tag, positions); //I am not sure if need this
        } else {
            System.out.println("Something went wrong with tag initialization of term " + value);
            System.exit(-1);
        }
    }

    public IndexTerm(String value) {
        this.value = value;

        df = 0;

        perDocumentTagOccurences = new TreeMap<>();
        perDocumentTagPositions = new TreeMap<>();
        perDocumentTF = new TreeMap<>();
    }

    public void addOccurence(String[] tags, String tag, int docID, int positionInTag) {
        if (!perDocumentTagOccurences.containsKey(docID)) {
            df++;

            perDocumentTagOccurences.put(docID, new HashMap<>());
            perDocumentTagPositions.put(docID, new HashMap<>());

            initializeTagOccurencesOfDocument(tags, docID);
            initializeTagPositionsOfDocument(tags, docID);
        }

        increaseOccurenceOfWordInTag(tag, docID);
        increasePositionOfWordInTag(tag, docID, positionInTag);
    }

    public int calculateOccurencesInDoc(int docID) {
        HashMap<String, Integer> tagOccurences = perDocumentTagOccurences.get(docID);
        
        if(tagOccurences == null){
            return 0;
        }
        
        int sum = 0;
        for (Map.Entry<String, Integer> entry : tagOccurences.entrySet()) {
            sum += entry.getValue();
        }
        return sum;
    }

    public void calculateTFinDoc(int docID, int maxOcc) {
        perDocumentTF.put(docID, calculateOccurencesInDoc(docID) * 1.0 / maxOcc * 1.0);
    }

    public String toString() {
        String s;

        s = "{" + value + "} => DF: " + df + " => " + perDocumentTagOccurences.toString();

        return s;
    }

}
