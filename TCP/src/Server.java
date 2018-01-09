import javax.xml.crypto.Data;
import java.io.*;
import java.util.*;
import java.net.*;
/**
 * Created by shubham on 18/10/17.
 */
public class Server {
	private DatagramSocket dskt;
	private int port;
	private int DATAGRAM_LENGTH = 1000;
	private byte[] recvBuffer;
	private byte[] sendBuffer;
	private int BUFFER_SIZE= 1000;
	private DatagramSocket receiverSocket;
	private boolean isConnected;
	private DatagramPacket recvPacket;
	private DatagramPacket sendPkt;
	private HashMap<String, Integer> clientMap;
	public Server(int port)
	{
		recvBuffer = new byte[BUFFER_SIZE];
		sendBuffer = new byte[BUFFER_SIZE];
		clientMap = new HashMap<String,Integer>();
		try {
//			dskt = new DatagramSocket(port);
			receiverSocket = new DatagramSocket(port);
			isConnected = true;
			while(isConnected){
				recvBuffer = new byte[1024];
				recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
				try {
					receiverSocket.receive(recvPacket);
					Packet p = datagramToPacket(recvPacket);
					int checksum = p.getData().length();
					int seqNo = p.getSeqNo();
					Packet p2 = new Packet(p.getSeqNo(),"ACK",1);
					int tport = recvPacket.getPort();
					InetAddress addr = recvPacket.getAddress();
					String cId = addr.toString() + Integer.toString(tport);
					System.out.println(cId);
					Integer value = clientMap.get(cId);
					// if Not new client
					if (value != null) {
						if(seqNo<=value){
							System.out.println("Duplicate Packet: " + p.getData());
						}
						else if(seqNo==value+1 && p.getDrop()==0 && p.getChecksum()==checksum){
							clientMap.put(cId,seqNo);
							System.out.println("Receiver data: " + p.getData());
							p2.setACK(clientMap.get(cId));
							sendBuffer = packetToDatagram(p2);
							sendPkt = new DatagramPacket(sendBuffer, sendBuffer.length, addr, tport);
							System.out.println("ACK: " +p2.getACK());
							receiverSocket.send(sendPkt);
						}
						else
						{
							p2.setACK(clientMap.get(cId));
							sendBuffer = packetToDatagram(p2);
							sendPkt = new DatagramPacket(sendBuffer, sendBuffer.length, addr, tport);
							System.out.println("ACK: " +p2.getACK());
							receiverSocket.send(sendPkt);
						}
					} else {
						clientMap.put(cId,seqNo);
						System.out.println("Receiver data: " + p.getData());
						p2.setACK(clientMap.get(cId));
						sendBuffer = packetToDatagram(p2);
						sendPkt = new DatagramPacket(sendBuffer, sendBuffer.length, addr, tport);
						System.out.println("ACK: " +p2.getACK());
						receiverSocket.send(sendPkt);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			receiverSocket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	private Packet datagramToPacket(DatagramPacket recvPkt) {
		ByteArrayInputStream bais = new ByteArrayInputStream(recvPkt.getData());
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			Packet p = (Packet) ois.readObject();
			return p;
		}  catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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


	public static void main(String[] args) {
		new Server(8080);
	}
}
