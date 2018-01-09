import java.io.Serializable;

/**
 * Created by shubham on 3/11/17.
 */
public class Packet implements Serializable {
    private static final long serialVersionUID;

    static {
        serialVersionUID = 1L;
    }
    private String data;
    private int seqNo,ACK,drop,checksum,bufferSize = 1024;
    public Packet(int seqNo,String data,int drop) {
        this.seqNo = seqNo;
        this.data = data;
        this.drop = drop;
    }

    public String toString() {
        return "ID: " + getSeqNo() + ", Data: " + getData() + ";";
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public int getACK() {
        return ACK;
    }

    public void setACK(int ACK) {
        this.ACK = ACK;
    }

    public int getDrop() {
        return drop;
    }

    public void setDrop(int drop) {
        this.drop = drop;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
