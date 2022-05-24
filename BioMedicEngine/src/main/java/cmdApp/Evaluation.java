/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmdApp;

import java.io.IOException;
import qualityEvaluator.IRQualityEvaluator;

/**
 *
 * @author manos
 */
public class Evaluation {

    public static void main(String[] args) throws Exception {
        IRQualityEvaluator iqe = new IRQualityEvaluator();

        String basePath = "C://BioMedicIndexer/";
        String resultsPath = "./corpus/results.txt";
        String versionRunName = "classic-biomedic-engine";
        iqe.createResultFileOfBioMedicIndexer(basePath, resultsPath, versionRunName);

        String givenResultsFilepath = "./corpus/qrels.txt";
        String reportFilepath = "./corpus/evaluation-results.txt";
        iqe.calculateBPREFinMemory(resultsPath, givenResultsFilepath, reportFilepath);
    }
}
