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

        String basePath = "C://BioMedicIndexer_2/";
        String resultsPath = "./qualityEvaluation/results-topic-weighting-50-50-set.txt";
        String versionRunName = "topic-biomedic-engine-weighting-50-50-set";
        iqe.createResultFileOfBioMedicIndexer(basePath, resultsPath, versionRunName);

        String givenResultsFilepath = "./corpus/qrels.txt";
        String reportFilepath = "./qualityEvaluation/evaluation-results-topic-weighting-50-50-set.txt";
        iqe.calculateBPREFinMemory(resultsPath, givenResultsFilepath, reportFilepath);
    }
}
