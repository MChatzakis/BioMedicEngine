/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalStructures;

import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class DocumentResult {

    private Doc doc;
    private double score;
    private String snippet;

    public DocumentResult(Doc doc, double score, String snippet) {
        this.doc = doc;
        this.score = score;
        this.snippet = snippet;
    }

    public DocumentResult(Doc doc, double socre) {
        this.doc = doc;
        this.score = score;

        snippet = "This is just an example snippet.";
    }

    public String toString() {
        return doc + " score: " + score + " snippet: " + snippet;
    }

}
