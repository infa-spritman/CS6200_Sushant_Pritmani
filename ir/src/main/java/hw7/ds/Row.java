package hw7.ds;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sushant on 8/13/2017.
 */
public class Row {

    private int label;
    private Map<Integer,Double> valuePair = new HashMap<>();

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public Map<Integer, Double> getValuePair() {
        return valuePair;
    }

    public void setValuePair(Map<Integer, Double> valuePair) {
        this.valuePair = valuePair;
    }
}
