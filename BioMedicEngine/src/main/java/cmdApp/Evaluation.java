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

        String basePath = "C://BioMedicIndexer_2/";
        String givenResultsFilepath = "./corpus/qrels.txt";

        double[] topicWeights = {0.0, 0.1, 0.3, 0.5, 0.7, 0.9, 1.0};
        boolean[] intersections = {false, true};

        for (double w : topicWeights) {

            for (boolean i : intersections) {
                String resultsPath = "./qualityEvaluation/results-topic-weighting-" + w + "-iset_" + i + ".txt";
                String versionRunName = "topic-biomedic-engineweighting-" + w + "-iset_" + i;

                IRQualityEvaluator iqe = new IRQualityEvaluator();
                iqe.createResultFileOfBioMedicIndexer(basePath, resultsPath, versionRunName, w, i);

                String reportFilepath = "./qualityEvaluation/evaluation-results-topic-weighting-" + w + "-iset_" + i + ".txt";
                iqe.calculateBPREFinMemory(resultsPath, givenResultsFilepath, reportFilepath);
            }

        }

    }
}
