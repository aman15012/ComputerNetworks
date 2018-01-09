/**
 * Created by shubham on 25/10/17.
 */
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private int port;
    private DatagramSocket cskt;
    private InetAddress ip;
    private int DATAGRAM_LENGTH;
    private byte[] recvBuffer;
    private byte[] sendBuffer;
    private int BUFFER_SIZE = 1024;
    private Vector<Packet> packetList = new Vector<Packet>();
    private static AtomicInteger windowSize = null;
    private static AtomicInteger countACK = null;
    private static AtomicInteger currACK = null;
    private static AtomicInteger sentLast = null;
    private static AtomicInteger timeUp = null;
    private Timer timer;
    private int nop = 25;
    private Random rand = new Random();
    public Client(Integer port) throws IOException {
        windowSize = new AtomicInteger(1);
        countACK = new AtomicInteger(0);
        currACK = new AtomicInteger(0);
        sentLast = new AtomicInteger(0);
        timeUp = new AtomicInteger(0);
        recvBuffer = new byte[BUFFER_SIZE];
        timer = new Timer();
        cskt = new DatagramSocket();
        ip = InetAddress.getLocalHost();
        this.port = port;
        receivePacket.start();
        sendPacket.start();
    }

    Thread receivePacket = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                recvBuffer = new byte[BUFFER_SIZE];
                boolean recievedCorrectly = false;
                DatagramPacket recvPkt = new DatagramPacket(recvBuffer,recvBuffer.length);
                try{
                    cskt.receive(recvPkt);
                    timeUp.set(0);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            timeUp.set(1);
                        }
                    },5);
                    Packet p = datagramToPacket(recvPkt);
                    System.out.println("CountACK: "+countACK);
                    System.out.println("Timer: "+ timeUp.get());
                    System.out.println("Sender data: " + p.getData()+ " " + p.getACK());
                    System.out.println(windowSize.get());
                    if(p.getACK() > currACK.get())
                    {
                        currACK.set(p.getACK());
                        countACK.set(1);
                    }
                    else if(p.getACK() == currACK.get()){
                        countACK.set(countACK.get()+1);
                    }
                    if(countACK.get()>=4 && windowSize.get()>1)
                        windowSize.set(windowSize.get()/2);
                    if(countACK.get()==1)
                        windowSize.set(windowSize.get()+1);
                    if(p.getData().equalsIgnoreCase("END"))
                        break;
                } catch (IOException e) {
                    System.out.println("Connection Lost to Server...");
                    e.printStackTrace();
                }
            }
        }
    });

    private Packet datagramToPacket(DatagramPacket recvPkt) {
        ByteArrayInputStream bais = new ByteArrayInputStream(recvPkt.getData());
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            Packet p = (Packet) ois.readObject();
            return p;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] packetToDatagram(Packet p) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] sendBuffer = new byte[BUFFER_SIZE];
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(p);
            sendBuffer = baos.toByteArray();
            return sendBuffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    Thread sendPacket = new Thread(new Runnable() {
        @Override
        public void run() {
            int temp = 1;
            sendBuffer = new byte[BUFFER_SIZE];
            Packet p = new Packet(0,"Hey, Trying connection to server... First Packet",0);
            p.setChecksum(p.getData().length());
            System.out.println("Sending: " + p.getData() + "DropStatus: " + p.getDrop());
            sendBuffer = packetToDatagram(p);
            DatagramPacket sendPkt = new DatagramPacket(sendBuffer,sendBuffer.length,ip,port);
            try {
                cskt.send(sendPkt);
                while(true)
                {
                    // What to send if 3 Duplicate ACKS are recieved
//                    System.out.println("CO: "+countACK.get());
                    if(countACK.get() >= 4 || timeUp.get()==1){

                        if(timeUp.get()==1) {
                            System.out.println("Resending Data due to timeout");
                            windowSize.set(1);
                            System.out.println("Setting window Size: 1");
                        }
                        timeUp.set(0);
                        int i = currACK.get()+1;
                        while(i <= windowSize.get()+currACK.get() && i <=nop)
                        {

                            p = new Packet(i,"packet "+Integer.toString(i),0);
                            int n = rand.nextInt(100);
                            if(windowSize.get()==1)
                                n = 0;
                            else if(n>10 || i==1)
                                n = 0;
                            else
                                n = 1;
                            p.setChecksum(p.getData().length());
                            sendBuffer = packetToDatagram(p);
                            sendPkt = new DatagramPacket(sendBuffer,sendBuffer.length,ip,port);
                            cskt.send(sendPkt);
                            System.out.println("Sender sent: " + p.getData() + " Drop? " + p.getDrop());
                            i++;
                        }
                        sentLast.set(currACK.get()+windowSize.get());
                        countACK.set(0);
                    }
                    if(currACK.get()>=nop)
                        break;
                    if(countACK.get()==1 && windowSize.get() + currACK.get() > sentLast.get())
                    {
                        int i = sentLast.get()+1;
                        System.out.println("Haha" + sentLast.get());
                        while(i <= windowSize.get()+currACK.get() && i <=nop)
                        {
                            p = new Packet(i,"packet "+Integer.toString(i),0);
                            int n = rand.nextInt(100);
                            if(windowSize.get()==1)
                                n = 0;
                            else if(n>10 || i==1)
                                n = 0;
                            else
                                n = 1;
                            p.setChecksum(p.getData().length());
                            p.setDrop(n);
                            sendBuffer = packetToDatagram(p);
                            sendPkt = new DatagramPacket(sendBuffer,sendBuffer.length,ip,port);
                            cskt.send(sendPkt);
                            System.out.println("Sender sent: " + p.getData() + " Drop? " + p.getDrop());
                            i++;
                        }
                        sentLast.set(currACK.get()+windowSize.get());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            sendPkt = new DatagramPacket(sendBuffer,sendBuffer.length,ip,port);
            try {
                cskt.send(sendPkt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    public static void main(String[] args) throws IOException {
        new Client(8080);
    }
}
