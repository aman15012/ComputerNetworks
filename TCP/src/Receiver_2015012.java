import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Vector;

public class Receiver_2015012 {
    private DatagramSocket receiverSocket;
    private int noClients;
    private boolean isConnected;
    private byte[] Rdata;
    private byte[] Sdata;
    private int flowSize;
    private HashMap<String, Vector<Integer> > clientMap;
    private HashMap<Integer,Integer> clientNumberMap;
    class clientId{
        InetAddress addr;
        int port;
        clientId(InetAddress addr, int port){
            this.addr = addr;
            this.port = port;
        }

        public InetAddress getAddr() {
            return addr;
        }

        public int getPort() {
            return port;
        }

        public void setAddr(InetAddress addr) {
            this.addr = addr;
        }

        public void setPort(int port) {
            this.port = port;
        }
        public String toString(){
            return addr.toString() + Integer.toString(port);
        }
    }
    public Receiver_2015012() throws SocketException {
        System.out.println("Server(Receiver) Log:");
        receiverSocket = new DatagramSocket(4444);
        isConnected = true;
        noClients = 1;
        flowSize = 1;
        clientMap = new HashMap<>();
        clientNumberMap = new HashMap<>();
        while(isConnected){

            Rdata = new byte[1024];
            DatagramPacket packet = new DatagramPacket(Rdata, Rdata.length);
            try {
                receiverSocket.receive(packet);

                Rdata = packet.getData();
                ByteArrayInputStream in = new ByteArrayInputStream(Rdata);
                ObjectInputStream is = new ObjectInputStream(in);
                Packet_2015012 p = (Packet_2015012) is.readObject();
                int checksum = p.getData().length();

                int seqNo = p.getId();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                Sdata = new byte[1024];
                Packet_2015012 p2 = new Packet_2015012(p.getId(),"ACK",1);
                p2.setIndividualACK(seqNo);
                p2.setFlowSize(flowSize);
                int port = packet.getPort();
                if(p.getData()=="FIN" || seqNo==0){
                    System.out.println("From: Client" + clientNumberMap.get(port) + " Received Data: FIN");
                    System.out.println("Client" + clientNumberMap.get(port) + " Disconnected.");
                }
                else{

                    Integer num = clientNumberMap.get(port);
                    if (num == null) {
                        clientNumberMap.put(port,noClients);
                        noClients++;
                    }
                    InetAddress addr = packet.getAddress();
                    String cId = addr.toString() + Integer.toString(port);
                    Vector<Integer> value = clientMap.get(cId);
                    // Setting Cumelative ACK
                    if (value != null) {
                        if(seqNo<=value.get(0)){
                            System.out.println("From: Client" + clientNumberMap.get(port) + " Duplicate Packet: " + p.getData());
                        }
                        else if(seqNo==value.get(0)+1 && p.getDrop()==0 && p.getChecksum()==checksum){
                            clientMap.get(cId).set(0,seqNo);
                            System.out.println("From: Client" + clientNumberMap.get(port) + " Received data: " + p.getData());
                        }
                    } else {
                        if(seqNo==1){
                            System.out.println("From: Client" + clientNumberMap.get(port) + " Received data (Handshake): SYN");
                            System.out.println("From: Client" + clientNumberMap.get(port) + " Received data (Handshake): ACK");
                            System.out.println("Connection Established With Client" + clientNumberMap.get(port) + ".");
                        }
                        Vector<Integer> tempVector = new Vector<>();
                        tempVector.add(seqNo);
                        clientMap.put(cId,tempVector);
                        System.out.println("From: Client" + clientNumberMap.get(port) + " Received data: " + p.getData());
                    }
                    p2.setACK(clientMap.get(cId).get(0));
                    ObjectOutputStream os = new ObjectOutputStream(outputStream);
                    os.writeObject(p2);
                    Sdata = outputStream.toByteArray();
                    packet = new DatagramPacket(Sdata, Sdata.length, addr, port);

                    receiverSocket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        receiverSocket.close();
    }

    public static void main(String[] args) throws SocketException {
        Receiver_2015012 r = new Receiver_2015012();
    }

}