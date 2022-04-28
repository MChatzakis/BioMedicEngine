/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval;

import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class SearchTerm {
    private String value;
    private int df;
    private long fp;
    
    public SearchTerm(String value, int df, long fp){
        this.value = value;
        this.df = df;
        this.fp = fp;
    }
    
    
    
}
