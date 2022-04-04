/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class Term {

    private String value;
    private HashMap<String, Integer> tagOccurences;

    public Term(String value) {
        this.value = value;
        tagOccurences = new HashMap<>();
    }

    public void initializeTagOccurences(String[] tags) {
        for (String tag : tags) {
            tagOccurences.put(tag, 0);
        }
    }

    public void increaseOccurenceOfWordInTag(String tag) {
        if (tagOccurences.containsKey(tag)) {
            tagOccurences.put(tag, tagOccurences.get(tag) + 1);
        } else {
            System.out.println("Something went wrong with tag initialization of term " + value);
            System.exit(-1);
        }

    }

    public String toString() {
        String s = "{" + value + "} => " + tagOccurences.toString();
        return s;
    }

}
