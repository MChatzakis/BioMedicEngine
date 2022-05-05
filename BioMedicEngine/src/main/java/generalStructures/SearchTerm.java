package generalStructures;

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

    public SearchTerm(String value, int df, long fp) {
        this.value = value;
        this.df = df;
        this.fp = fp;
    }

}
