/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalStructures;

import java.util.TreeMap;
import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class SearchResult {

    private TreeMap<Double, DocResult> relevantDocuments;
    private double responseTime;

    public SearchResult(TreeMap<Double, DocResult> relevantDocuments, double responseTime) {
        this.responseTime = responseTime;
        this.relevantDocuments = relevantDocuments;
    }
    

}
