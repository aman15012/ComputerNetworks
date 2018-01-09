import java.io.Serializable;


public class Packet_2015012 implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;
    private String data;
    private int ACK;
    private int checksum;
    private int drop;
    private int flowSize;
    private int individualACK;
    public Packet_2015012(int id, String data, int drop) {
        this.id = id;
        this.data = data;
        this.drop = drop;
    }

    public int getIndividualACK() {
        return individualACK;
    }

    public int getACK() {
        return ACK;
    }

    public int getChecksum() {
        return checksum;
    }

    public int getDrop() {
        return drop;
    }

    public int getId() {
        return id;
    }
    public int getFlowSize(){
        return flowSize;
    }

    public String getData() {
        return data;
    }

    public void setACK(int ACK) {
        this.ACK = ACK;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDrop(int drop) {
        this.drop = drop;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFlowSize(int flowSize) {
        this.flowSize = flowSize;
    }

    public void setIndividualACK(int individualACK) {
        this.individualACK = individualACK;
    }

    public String toString() {
        return "Data = " + getData() + " ID = " + getId();
    }
}