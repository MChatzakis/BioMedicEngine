/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qualityEvaluator;

import commonUtilities.CommonUtilities;
import generalStructures.DocResult;
import generalStructures.SearchResult;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import retrieval.BioMedicRetriever;

/**
 *
 * @author manos
 */
public class IRQualityEvaluator {

    public IRQualityEvaluator() {

    }

    public void createResultFileOfBioMedicIndexer(String basePath, String resultsPath, String versionRunName) throws IOException {
        ArrayList<String> queries = CommonUtilities.readXML("corpus/topics.xml", "topic", "summary");
        ArrayList<String> types = CommonUtilities.readXMLattr("corpus/topics.xml", "topic", "type");

        //System.out.println(queries);
        //System.out.println(types);
        String vocabularyFile = basePath + "vocabulary.txt";
        String documentsFile = basePath + "documents.txt";
        String postingFile = basePath + "postings.txt";
        String normsFile = basePath + "vectors.txt";
        String mappingsFile = basePath + "mappings.txt";

        BioMedicRetriever bmr = new BioMedicRetriever(documentsFile, postingFile, vocabularyFile, normsFile, mappingsFile);

        for (int i = 0; i < queries.size(); i++) {
            String q = queries.get(i);
            String t = types.get(i);
            SearchResult results = bmr.findRelevantTopic(q, t);

            FileWriter fw = new FileWriter(resultsPath);

            ArrayList<DocResult> documents = results.getRelevantDocuments();

            if (documents.size() > 1000) {
                documents = new ArrayList<>(documents.subList(0, 1000));
            }

            int rank = 1;
            for (DocResult d : documents) {
                String topicNo = i + "";
                String q0 = "0";
                String pmcID = CommonUtilities.readPMCIDofFile(d.getDoc());
                String docRank = (rank++) + "";
                String score = d.getScore() + "";
                String runName = versionRunName;

                fw.write(topicNo + " " + q0 + " " + pmcID + " " + docRank + " " + score + " " + runName + "\n");
            }

            fw.close();
        }

    }

    public void calculateBPREFinMemory(String systemResultsFilepath, String givenResultsFilepath, String reportFilepath) throws Exception {
        TreeMap<Integer, BPREFData> topicData = new TreeMap<>();

        BufferedReader br = new BufferedReader(new FileReader(givenResultsFilepath));
        String line;

        while ((line = br.readLine()) != null) {
            String[] contents = line.split("\t");
            int topicNo = Integer.parseInt(contents[0]);
            int id = Integer.parseInt(contents[2]);
            int relScore = Integer.parseInt(contents[3]);

            
        }
    }
}
