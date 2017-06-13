package hw2.POJO;

import java.util.LinkedList;

/**
 * Created by Sushant on 6/5/2017.
 */
public class TermStat  implements Comparable<TermStat>{

    private Integer df;
    private Integer cf;
    private Integer tf;
    private String docId;
    private LinkedList<Integer> positions;

    public TermStat(Integer df, Integer cf, Integer tf, String docId, LinkedList<Integer> positions) {
        this.df = df;
        this.cf = cf;
        this.tf = tf;
        this.docId = docId;
        this.positions = positions;
    }

    public Integer getDf() {
        return df;
    }

    public void setDf(Integer df) {
        this.df = df;
    }

    public Integer getCf() {
        return cf;
    }

    public void setCf(Integer cf) {
        this.cf = cf;
    }

    public Integer getTf() {
        return tf;
    }

    public void setTf(Integer tf) {
        this.tf = tf;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public LinkedList<Integer> getPositions() {
        return positions;
    }

    public void setPositions(LinkedList<Integer> positions) {
        this.positions = positions;
    }

    @Override
    public int compareTo(TermStat o) {
        return o.getTf().compareTo(this.getTf());
    }
}
