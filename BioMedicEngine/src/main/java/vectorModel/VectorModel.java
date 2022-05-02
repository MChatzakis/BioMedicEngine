/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vectorModel;

import commonUtilities.CommonUtilities;
import generalStructures.Doc;
import generalStructures.SearchTerm;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author manos
 */
public class VectorModel {

    private TreeMap<String, SearchTerm> loadVocabulary(RandomAccessFile vocabularyRaf) throws IOException {
        TreeMap<String, SearchTerm> vocabulary = new TreeMap<>();
        vocabularyRaf.seek(0);

        String line;
        while ((line = vocabularyRaf.readUTF()) != null) {
            if (line.equals("#end")) {
                break;
            }

            String[] contents = line.split(" ");
            String value = contents[0];
            int df = Integer.parseInt(contents[1]);
            long ptr = Long.parseLong(contents[2]);

            vocabulary.put(value, new SearchTerm(value, df, ptr));
            System.out.println("Loaded term " + value);
        }
        vocabularyRaf.seek(0);

        return vocabulary;
    }

    public void initializeDocumentVectorsToFile(String documentsFilepath, String vocabularyFilepath, String postingsFilepath, String vectorsFilepath, String mappingsFilepath, int totalDocuments) throws FileNotFoundException, IOException {
        RandomAccessFile documentsRaf = new RandomAccessFile(documentsFilepath, "r");
        RandomAccessFile vocabularyRaf = new RandomAccessFile(vocabularyFilepath, "r");
        RandomAccessFile postingRaf = new RandomAccessFile(postingsFilepath, "r");

        RandomAccessFile vectorRaf = new RandomAccessFile(vectorsFilepath, "rw");

        TreeMap<String, SearchTerm> vocabulary = loadVocabulary(vocabularyRaf);
        TreeMap<Integer, Long> docVectorMappings = new TreeMap<>();

        int totalTerms = vocabulary.size();

        String line;
        while ((line = documentsRaf.readUTF()) != null) {
            if (line.equals("#end")) {
                break;
            }

            String[] contents = line.split(" ");
            int docID = Integer.parseInt(contents[0]);

            double[] docVector = new double[totalTerms];
            int currentDim = 0;
            for (Map.Entry<String, SearchTerm> entry : vocabulary.entrySet()) {
                //System.out.println(entry.getKey() + ":" + entry.getValue());
                SearchTerm t = entry.getValue();
                int df = t.getDf();
                double iDF = CommonUtilities.getIDF(df, totalDocuments);
                double tf = 0;
                //seek if this term is present in document with ID=docID
                postingRaf.seek(t.getFp());
                String cline;

                while ((cline = postingRaf.readUTF()) != null) { //this is just a dummy != to read the value inside while.
                    if (cline.equals("#stop\n") || cline.equals("#end")) {
                        break;
                    }

                    String[] pcontents = cline.split(" ");
                    int cDocId = Integer.parseInt(pcontents[0]);

                    if (cDocId == docID) {
                        tf = Double.parseDouble(pcontents[1]);
                        break;
                    } else if (cDocId > docID) {
                        break; //optimization
                    }

                }

                docVector[currentDim++] = tf * iDF;
            }

            //write line
            String vec = "";
            for (int i = 0; i < currentDim; i++) {
                vec += docVector[i] + "";
                if (i != currentDim - 1) {
                    vec += ",";
                }
            }

            docVectorMappings.put(docID, vectorRaf.getFilePointer());
            vectorRaf.writeUTF(docID + " " + vec + " \n");
        }

        vectorRaf.close();
        documentsRaf.close();
        vocabularyRaf.close();
        postingRaf.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(mappingsFilepath));
        for (Map.Entry<Integer, Long> entry : docVectorMappings.entrySet()) {
            writer.write(entry.getKey() + " " + entry.getValue() + "\n");
        }
        writer.close();

    }
}
