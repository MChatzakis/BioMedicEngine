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

        // If the object is compared with itself then return true 
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof BPREFDoc)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        BPREFDoc c = (BPREFDoc) o;

        // Compare the data members and return accordingly
        return Double.compare(id, c.id) == 0;
    }

}
