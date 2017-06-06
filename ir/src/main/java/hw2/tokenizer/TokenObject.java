package hw2.tokenizer;

/**
 * Created by Sushant on 6/5/2017.
 */
public class TokenObject {

    private String termId;
    private String docId;
    private String position;

    public TokenObject(String termId, String docId, String position) {
        this.termId = termId;
        this.docId = docId;
        this.position = position;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String positions) {
        this.position = positions;
    }
}
