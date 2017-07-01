package hw3.POJO;


/**
 * Created by Sushant on 6/27/2017.
 */
public class QueueElement implements Comparable<QueueElement> {

    private String URL;
    private String normalisedURL;
    private Integer inlinksCount;
    private Long timeStamp;
    private Double score;
    private Integer noOfkeywords;

    public QueueElement(String URL, String normalisedURL, Integer inlinksCount, Long timeStamp, Double score, Integer noOfkeywords) {
        this.URL = URL;
        this.normalisedURL = normalisedURL;
        this.inlinksCount = inlinksCount;
        this.timeStamp = timeStamp;
        this.score = score;
        this.noOfkeywords = noOfkeywords;
    }

    public void increaseInlinks(Integer delta) {

        this.inlinksCount += delta;
    }


    public void increaseScore(double delta) {

        this.score += delta;
    }

    public void increaseKeywords(Integer delta) {

        this.noOfkeywords += delta;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getNormalisedURL() {
        return normalisedURL;
    }

    public void setNormalisedURL(String normalisedURL) {
        this.normalisedURL = normalisedURL;
    }

    public Integer getInlinksCount() {
        return inlinksCount;
    }

    public void setInlinksCount(Integer inlinksCount) {
        this.inlinksCount = inlinksCount;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getNoOfkeywords() {
        return noOfkeywords;
    }

    public void setNoOfkeywords(Integer noOfkeywords) {
        this.noOfkeywords = noOfkeywords;
    }

    @Override
    public int compareTo(QueueElement q) {

        double v = .3 * q.getInlinksCount() + .7 * q.getNoOfkeywords();
        double currentElement = .3*(this.getInlinksCount()) + .7*(this.getNoOfkeywords());

//        double v = q.getNoOfkeywords();
//        double currentElement = this.getNoOfkeywords();
        if (v < currentElement) return -1;
        if (v > currentElement) return 1;
        return 0;


        //return q.inlinksCount.compareTo(this.getInlinksCount());
    }
}

