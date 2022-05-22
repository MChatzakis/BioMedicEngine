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
import java.util.Map;
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
        int prevTopicNo = -1;
        while ((line = br.readLine()) != null) {
            BPREFData currentTopicData = new BPREFData();

            String[] contents = line.split("\t");
            int topicNo = Integer.parseInt(contents[0]);
            int id = Integer.parseInt(contents[2]);
            int relScore = Integer.parseInt(contents[3]);

            if (prevTopicNo == -1) {
                prevTopicNo = topicNo;
            }

            if (prevTopicNo != topicNo) {
                topicData.put(topicNo, new BPREFData(currentTopicData));
                currentTopicData = new BPREFData(); //reset
                prevTopicNo = topicNo;
            }

            if (relScore > 0) {
                currentTopicData.addRelevantDocument(new BPREFDoc(id, relScore));
            } else {
                currentTopicData.addNonRelevantDocument(new BPREFDoc(id, relScore));
            }

        }

        TreeMap<Integer, ArrayList<BPREFDoc>> retrievedData = new TreeMap<>();
        br = new BufferedReader(new FileReader(systemResultsFilepath));

        prevTopicNo = -1;
        while ((line = br.readLine()) != null) {
            ArrayList<BPREFDoc> results = new ArrayList<>();

            String[] contents = line.split(" ");
            int topicNo = Integer.parseInt(contents[0]);
            int id = Integer.parseInt(contents[2]);
            double score = Double.parseDouble(contents[4]);

            if (prevTopicNo == -1) {
                prevTopicNo = topicNo;
            }

            if (prevTopicNo != topicNo) {
                retrievedData.put(topicNo, new ArrayList<>(results));
                results = new ArrayList<>();
                prevTopicNo = topicNo;
            }

            results.add(new BPREFDoc(id, score));
        }

        for(Map.Entry<>)
        
    }

}
