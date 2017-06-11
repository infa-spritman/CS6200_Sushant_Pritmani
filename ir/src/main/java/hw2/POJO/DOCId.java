package hw2.POJO;

/**
 * Created by Sushant on 6/9/2017.
 */
public class DOCId {

    private String docNo;
    private Integer docLength;

    public DOCId(String docNo, Integer docLength) {
        this.docNo = docNo;
        this.docLength = docLength;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public Integer getDocLength() {
        return docLength;
    }

    public void setDocLength(Integer docLength) {
        this.docLength = docLength;
    }
}
