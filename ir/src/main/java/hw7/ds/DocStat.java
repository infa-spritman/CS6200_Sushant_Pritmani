package hw7.ds;

/**
 * Created by Sushant on 8/12/2017.
 */
public class DocStat {



    private String term;
    private Integer df;

    public DocStat(String term, Integer df) {
        this.term = term;
        this.df = df;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Integer getDf() {
        return df;
    }

    public void setDf(Integer df) {
        this.df = df;
    }
}
