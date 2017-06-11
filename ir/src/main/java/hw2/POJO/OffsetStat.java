package hw2.POJO;

/**
 * Created by Sushant on 6/7/2017.
 */
public class OffsetStat {

    private Integer offset;
    private Integer length;

    public OffsetStat(Integer offset, Integer length) {
        this.offset = offset;
        this.length = length;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
