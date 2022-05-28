/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qualityEvaluator;

import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class BPREFDoc {

    int id;
    int relevancyScore;

    double score;

    public BPREFDoc(int id, int relevancyScore) {
        this.id = id;
        this.relevancyScore = relevancyScore;
    }

    public BPREFDoc(int id, double score) {
        this.id = id;
        this.score = score;
    }

    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof BPREFDoc)) {
            return false;
        }

        BPREFDoc c = (BPREFDoc) o;

        return Double.compare(id, c.id) == 0;
    }

    public int hashCode() {
        return this.id;
    }
}
