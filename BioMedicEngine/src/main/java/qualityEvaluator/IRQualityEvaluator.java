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

    private double calculateBPREF(BPREFData data, ArrayList<BPREFDoc> retrievedDocuments) {
        double score = 0.0;

        ArrayList<BPREFDoc> judgedRelevantDocuments = data.getRelevantDocuments();
        ArrayList<BPREFDoc> judgedNonRelevantDocuments = data.getRelevantDocuments();

        int R = judgedRelevantDocuments.size();
        int N = judgedNonRelevantDocuments.size();

        double denom = Math.min(R, N) * 1.0;

        int nonRelevantDocumentCount = 0;
        for (int i = 0; i < retrievedDocuments.size(); i++) {
            BPREFDoc retrievedDoc = retrievedDocuments.get(i);
            double score2add = 0;

            if (judgedRelevantDocuments.contains(retrievedDoc)) {
                score2add = 1 - (nonRelevantDocumentCount / denom);
            } else if (judgedNonRelevantDocuments.contains(retrievedDoc)) {
                nonRelevantDocumentCount++; //to eida apo ta slides
            }

            score += score2add;
        }

        score /= R; //last step
        return score;
    }

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
        FileWriter fw = new FileWriter(resultsPath);

        for (int i = 0; i < queries.size(); i++) {
            String q = queries.get(i);
            String t = types.get(i);
            t = t.replace("type=", "");
            t = t.substring(1, t.length() - 1);
            //System.out.println("topic->" + t);
            //if (t.equals("diagnosis")) {
            //   System.out.println("eleos...");
            //}
            SearchResult results = bmr.findRelevantTopic(q, t);

            ArrayList<DocResult> documents = results.getRelevantDocuments();

            System.out.println("Documents " + documents.size());
            if (documents.size() > 1000) {
                documents = new ArrayList<>(documents.subList(0, 1000));
            }

            int rank = 1;
            for (DocResult d : documents) {
                String topicNo = (i + 1) + "";
                String q0 = "0";
                String pmcID = CommonUtilities.readPMCIDofFile(d.getDoc());
                String docRank = (rank++) + "";
                String score = d.getScore() + "";
                String runName = versionRunName;

                fw.write(topicNo + " " + q0 + " " + pmcID + " " + docRank + " " + score + " " + runName + "\n");
            }

        }
        fw.close();
    }

    public void calculateBPREFinMemory(String systemResultsFilepath, String givenResultsFilepath, String reportFilepath) throws Exception {
        TreeMap<Integer, BPREFData> topicData = new TreeMap<>();

        BufferedReader br = new BufferedReader(new FileReader(givenResultsFilepath));
        String line;
        int prevTopicNo = -1;
        BPREFData currentTopicData = new BPREFData();
        while ((line = br.readLine()) != null) {

            String[] contents = line.split("\t");

            int topicNo = Integer.parseInt(contents[0]);
            int id = Integer.parseInt(contents[2]);
            int relScore = Integer.parseInt(contents[3]);

            if (prevTopicNo == -1) {
                prevTopicNo = topicNo;
            }

            if (prevTopicNo != topicNo) {
                //System.out.println("Topic Changed from " + prevTopicNo + " to " + topicNo);
                //System.out.println(currentTopicData);
                topicData.put(prevTopicNo, new BPREFData(currentTopicData));
                //System.out.println(topicData);
                currentTopicData = new BPREFData(); //reset
                prevTopicNo = topicNo;
            }

            if (relScore > 0) {
                currentTopicData.addRelevantDocument(new BPREFDoc(id, relScore));
            } else {
                currentTopicData.addNonRelevantDocument(new BPREFDoc(id, relScore));
            }

        }
        topicData.put(prevTopicNo, new BPREFData(currentTopicData));

        System.out.println(topicData);
        System.out.println("Topic Data Size = " + topicData.size());

        TreeMap<Integer, ArrayList<BPREFDoc>> retrievedData = new TreeMap<>();
        br = new BufferedReader(new FileReader(systemResultsFilepath));

        prevTopicNo = -1;
        ArrayList<BPREFDoc> results = new ArrayList<>();
        while ((line = br.readLine()) != null) {

            String[] contents = line.split(" ");
            int topicNo = Integer.parseInt(contents[0]);
            int id = Integer.parseInt(contents[2]);
            double score = Double.parseDouble(contents[4]);

            if (prevTopicNo == -1) {
                prevTopicNo = topicNo;
            }

            if (prevTopicNo != topicNo) {
                retrievedData.put(prevTopicNo, new ArrayList<>(results));
                results = new ArrayList<>();
                prevTopicNo = topicNo;
            }

            results.add(new BPREFDoc(id, score));
        }

        retrievedData.put(prevTopicNo, new ArrayList<>(results));
        System.out.println(retrievedData);

        FileWriter fw = new FileWriter(reportFilepath);
        fw.write("TopicNO\tBPREF\n");
        for (Map.Entry<Integer, BPREFData> entry : topicData.entrySet()) {
            int topicNumber = entry.getKey();
            BPREFData dataOfTopic = entry.getValue();

            ArrayList<BPREFDoc> documentsGivenForTopic = retrievedData.get(topicNumber);

            double scoreBPREF = calculateBPREF(dataOfTopic, documentsGivenForTopic);

            fw.write(topicNumber + "\t" + scoreBPREF + "\n");

        }

        fw.close();

    }

}
