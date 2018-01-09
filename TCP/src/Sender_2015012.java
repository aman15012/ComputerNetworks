import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Sender_2015012 {
    private DatagramSocket socket;
    private InetAddress addr;
    private int dataSize;
    private byte[] data;
    private byte[] data2;
    private Timer timer;
    private static final AtomicInteger windowSize = new AtomicInteger();
    private static final AtomicInteger ACKcount = new AtomicInteger();
    private static final AtomicInteger currentACK = new AtomicInteger();
    private static final AtomicInteger lastSent = new AtomicInteger();
    private static final AtomicInteger timeUp = new AtomicInteger();
    private static final AtomicInteger incrementACK = new AtomicInteger();

    Random rand;
    public Sender_2015012() throws IOException {
        socket = new DatagramSocket();
        addr = InetAddress.getLocalHost();
        ACKcount.set(0);
        currentACK.set(0);
        windowSize.set(1);
        lastSent.set(0);
        timeUp.set(0);
        incrementACK.set(1);
        dataSize = 10; // Default
        rand = new Random();
        Thread thread1 = new Thread(new sendMessage());
        Thread thread2 = new Thread(new receiveACK());
        thread1.start();
        thread2.start();

    }

    class receiveACK implements Runnable {
        public void run() {
            while (true) {

                data2 = new byte[1024];
                DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
                try {
                    socket.receive(packet2);
                    timer = new Timer();
                    timeUp.set(0);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            timeUp.set(1);
                        }
                    }, 15000); //timeout time
                    data2 = packet2.getData();
                    ByteArrayInputStream in = new ByteArrayInputStream(data2);
                    ObjectInputStream is = new ObjectInputStream(in);
                    Packet_2015012 p = (Packet_2015012) is.readObject();
//                    System.out.println(windowSize.get());
                    System.out.println("Cumulative " + p.getData() + ": " + p.getACK() + " Individual Packet " + p.getData() + ": " + p.getIndividualACK());
//                    System.out.println("Current " + currentACK.get() + " " + "count: " + ACKcount.get() + " " + "get: " + p.getACK());
                    if(p.getACK()==currentACK.get()){
                        ACKcount.set(ACKcount.get()+1);
                    }
                    else if(p.getACK()<currentACK.get()) {

                    }
                    else{
                        currentACK.set(p.getACK());
                        ACKcount.set(1);
                    }
                    if((ACKcount.get()>=4)  && windowSize.get()>=2){
                        windowSize.set(windowSize.get()/2);
                        System.out.println("Triple Duplicate ACK.\nNew Window Size: " + windowSize.get());
                    }
                    else if(ACKcount.get()==1 && currentACK.get()>=incrementACK.get() && lastSent.get()<currentACK.get()+windowSize.get()){
                        if(windowSize.get() >= p.getFlowSize()){
                            System.out.println("Limiting Window Size - Flow Control.");
                        }
                        else{

                            windowSize.set(windowSize.get()+1);
                            System.out.println("New Window Size: " + windowSize.get());
                        }

                    }
                    if(p.getData().equalsIgnoreCase("END")){
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class sendMessage implements Runnable {
        public void run() {
            int count =1;
            // Assuming initial window size is always 1
            System.out.println("Message from Server: SYN-ACK.");
            System.out.println("Connection Established.");
            Packet_2015012 p = new Packet_2015012(1,"packet"+Integer.toString(count),0); // First packet doesn't drop
            p.setChecksum(p.getData().length());
            lastSent.set(1);
//            System.out.println("Window Size: " + windowSize.get());
            System.out.println("Sender sent: " + p.getData() + " Drop? " + p.getDrop());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream os = new ObjectOutputStream(outputStream);
                os.writeObject(p);
                data = outputStream.toByteArray();
                DatagramPacket packet = new DatagramPacket(data, data.length, addr, 4444);
                socket.send(packet);
//                windowSize.set(windowSize.get()+1);
                while (true) {
                    if(ACKcount.get()>=4 || timeUp.get()==1 ){
//                        System.out.println("window: " + windowSize.get()+ "timer: " + timeUp.get()+ "ackcount: " + ACKcount.get());
                        if(timeUp.get()==1){
                            System.out.println("Timer Triggered. Resending data.");
                            if(windowSize.get()>=2){
                                windowSize.set(1);
                                System.out.println("New Window Size: " + windowSize.get());
                            }
                        }
                        timeUp.set(0);
                        for(int i=currentACK.get()+1;i<=currentACK.get()+windowSize.get();i++){
                            if(i>dataSize){
                                break;
                            }
                            p = new Packet_2015012(i,"packet"+Integer.toString(i),1);
                            p.setChecksum(p.getData().length());
                            int n = rand.nextInt(2);
                            if(windowSize.get()==1 || i==currentACK.get())
                                n=0;
                            p.setDrop(n);
//                            System.out.println("Window Size: " + windowSize.get());
                            System.out.println("Sender sent: " + p.getData() + " Drop? " + p.getDrop());
                            outputStream = new ByteArrayOutputStream();

                            os = new ObjectOutputStream(outputStream);
                            os.writeObject(p);
                            data = outputStream.toByteArray();
                            packet = new DatagramPacket(data, data.length, addr, 4444);
                            socket.send(packet);
                        }
                        lastSent.set(currentACK.get()+windowSize.get());
                        incrementACK.set(lastSent.get());
                        ACKcount.set(0);

                    }
                    if(ACKcount.get()==1 && lastSent.get()<currentACK.get()+windowSize.get()){
//                        System.out.println("ackcount: " + ACKcount );
                        for(int i=lastSent.get()+1;i<=currentACK.get()+windowSize.get();i++){
                            if(i>dataSize){
                                break;
                            }
                            p = new Packet_2015012(i,"packet"+Integer.toString(i),1);
                            p.setChecksum(p.getData().length());
                            int n = rand.nextInt(2);
                            if(windowSize.get()==1)
                                n=0;
                            p.setDrop(n);
//                            System.out.println("Window Size: " + windowSize.get());
                            System.out.println("Sender sent: " + p.getData() + " Drop? " + p.getDrop());
                            outputStream = new ByteArrayOutputStream();

                            os = new ObjectOutputStream(outputStream);
                            os.writeObject(p);
                            data = outputStream.toByteArray();
                            packet = new DatagramPacket(data, data.length, addr, 4444);
                            socket.send(packet);
                        }
                        lastSent.set(currentACK.get()+windowSize.get());
                        incrementACK.set(lastSent.get());

                    }

                    if(currentACK.get()>=dataSize){
                        System.out.println("Transfer Complete.");
                        Packet_2015012 p_ = new Packet_2015012(0,"FIN",0);
                        ByteArrayOutputStream OutputStream = new ByteArrayOutputStream();
                        try {
                            ObjectOutputStream Os = new ObjectOutputStream(OutputStream);
                            Os.writeObject(p_);
                            data = OutputStream.toByteArray();
                            DatagramPacket packet_ = new DatagramPacket(data, data.length, addr, 4444);
                            socket.send(packet_);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.exit(0);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void close(){
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Client(Sender) Log:");
        Sender_2015012 s = new Sender_2015012();

    }
}