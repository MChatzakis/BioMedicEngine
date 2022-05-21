/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qualityEvaluator;

import java.util.ArrayList;
import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class BPREFData {

    private ArrayList<BPREFDoc> relevantDocuments;
    private ArrayList<BPREFDoc> nonRelevantDocuments;

    public BPREFData() {
        relevantDocuments = new ArrayList<>();
        nonRelevantDocuments = new ArrayList<>();
    }

    public BPREFData(BPREFData d) {
        relevantDocuments = new ArrayList<>(d.getRelevantDocuments());
        nonRelevantDocuments = new ArrayList<>(d.getNonRelevantDocuments());
    }

    public void addRelevantDocument(BPREFDoc d) {
        relevantDocuments.add(d);
    }

    public void addNonRelevantDocument(BPREFDoc d) {
        nonRelevantDocuments.add(d);
    }
}
