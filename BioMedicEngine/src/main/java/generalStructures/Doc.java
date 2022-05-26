/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generalStructures;

import lombok.Data;

/**
 *
 * @author Manos Chatzakis
 */
@Data
public class Doc {

    private int id;
    private long norm;
    private String path;
    private long docFilePointer;

    public Doc(int id, String path) {
        this.id = id;
        this.path = path;

        norm = 0;
        docFilePointer = 0;
        docFilePointer = 0;
    }

    public String toString() {
        return "{" + id + "," + path + "}";
    }
    
    public boolean equals(Object o) {

        // If the object is compared with itself then return true 
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Doc)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Doc c = (Doc) o;

        // Compare the data members and return accordingly
        return Double.compare(id, c.id) == 0;
    }
}
