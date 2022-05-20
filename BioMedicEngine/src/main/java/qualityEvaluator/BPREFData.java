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

    private ArrayList<Integer> relevantDocumentsPMCID;
    private ArrayList<Integer> nonRelevantDocumentsPMCID;

    public BPREFData() {
        relevantDocumentsPMCID = new ArrayList<>();
        nonRelevantDocumentsPMCID = new ArrayList<>();
    }

    public void addRelevantDocument(Integer pmcID) {
        relevantDocumentsPMCID.add(pmcID);
    }

    public void addNonRelevantDocument(Integer pmcID) {
        nonRelevantDocumentsPMCID.add(pmcID);
    }
}
