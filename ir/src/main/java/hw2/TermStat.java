package hw2;

import java.util.LinkedList;

/**
 * Created by Sushant on 6/5/2017.
 */
public class TermStat {

    private double df;
    private double cf;
    private double tf;
    private String docId;
    private LinkedList<Integer> positions;

    public TermStat(double df, double cf, double tf, String docId, LinkedList<Integer> positions) {
        this.df = df;
        this.cf = cf;
        this.tf = tf;
        this.docId = docId;
        this.positions = positions;
    }

    public double getDf() {
        return df;
    }

    public void setDf(double df) {
        this.df = df;
    }

    public double getCf() {
        return cf;
    }

    public void setCf(double cf) {
        this.cf = cf;
    }

    public double getTf() {
        return tf;
    }

    public void setTf(double tf) {
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
}
