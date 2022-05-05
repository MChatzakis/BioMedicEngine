/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package generalStructures;

import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class IndexResult {

    private double totalTime;
    private double partitioningTime;
    private double mergingTime;
    private double normCalculationTime;

    private int partialTH;
    private int totalDocuments;

    private String directoryBasePath;
    private String directoryOutputPath;

    public IndexResult(double totalTime, double partitioningTime, double mergingTime, double normCalculationTime, int partialTH, int totalDocuments, String directoryBasePath, String directoryOutputPath) {
        this.totalTime = totalTime;
        this.partitioningTime = partitioningTime;
        this.mergingTime = mergingTime;
        this.normCalculationTime = normCalculationTime;
        this.partialTH = partialTH;
        this.totalDocuments = totalDocuments;
        this.directoryBasePath = directoryBasePath;
        this.directoryOutputPath = directoryOutputPath;
    }

    public String toString() {
        String res = "";

        res += "========= BioMedic Indexer Results =========\n";
        res += "Threshold of terms: " + partialTH + "\n";
        res += "Directory of documents indexed: " + directoryBasePath + "\n";
        res += "Directory of indexer output: " + directoryOutputPath + "\n";
        res += "Total Documents Indexed: " + totalDocuments + "\n";
        res += "Time Elapsed for Partitioning Phase (seconds): " + partitioningTime / 1000000000.0 + "\n";
        res += "Time Elapsed for Merging Phase (seconds): " + mergingTime / 1000000000.0 + "\n";
        res += "Time Elapsed for Norm Calculation phase (seconds): " + normCalculationTime / 1000000000.0 + "\n";
        res += "Total Time Elapsed (seconds): " + totalTime / 1000000000.0 + "\n";
        res += "=======================================\n";

        return res;
    }
}
