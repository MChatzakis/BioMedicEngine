/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structures;

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
    }

    public String toString() {
        return "{" + id + " => " + path + "=> " + norm + "}";
    }
}
