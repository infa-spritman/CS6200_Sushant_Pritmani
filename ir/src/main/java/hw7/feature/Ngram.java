package hw7.feature;

import org.elasticsearch.common.settings.Settings;

/**
 * Created by Sushant on 8/11/2017.
 */
public class Ngram {

    private boolean indexPresent;
    private Integer index;
    private String ngram ="unknown";
    private int slop =0;
    private boolean inOrder = true;

    public boolean isIndexPresent() {
        return indexPresent;
    }

    public void setIndexPresent(boolean indexPresent) {
        this.indexPresent = indexPresent;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
        this.indexPresent = true;
    }

    public String getNgram() {
        return ngram;
    }

    public void setNgram(String ngram) {
        this.ngram = ngram;
    }

    public int getSlop() {
        return slop;
    }

    public void setSlop(int slop) {
        this.slop = slop;
    }

    public boolean isInOrder() {
        return inOrder;
    }

    public void setInOrder(boolean inOrder) {
        this.inOrder = inOrder;
    }

   @Override
   public String toString(){
        final StringBuilder sb = new StringBuilder("Ngram{");
        if(indexPresent)
            sb.append("index=").append(index).append(",");

        sb.append("ngram=").append(ngram);
        sb.append(", slop=").append(slop);
        sb.append(", inOrder=").append(inOrder);
        sb.append('}');

        return sb.toString();

   }

}
