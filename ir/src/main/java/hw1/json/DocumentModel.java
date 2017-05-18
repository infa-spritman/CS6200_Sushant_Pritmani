package hw1.json;

import java.util.List;

/**
 * Created by Sushant on 5/12/2017.
 */
public class DocumentModel {

    private String docno;
    private String fileID;
    private String first;
    private String second;
    private List<String> head;
    private List<String> byline;
    private String dateline;
    private String text;

    public String getDocno() {
        return docno;
    }

    public void setDocno(String docno) {
        this.docno = docno;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public List<String> getHead() {
        return head;
    }

    public void setHead(List<String> head) {
        this.head = head;
    }

    public List<String> getByline() {
        return byline;
    }

    public void setByline(List<String> byline) {
        this.byline = byline;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
