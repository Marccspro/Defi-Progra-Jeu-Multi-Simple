package fr.veridiangames.main.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

public class Client {
	private DatagramSocket socket;
	public InetAddress ip;
	public int port;
	
	public int id;
	public String name;
	
	public Client(String name, String ip, int port) {
		this.id = new Random().nextInt();
		this.name = name;
		
		if (!connect(ip, port)) {
			System.err.println("Connection failed !");
			System.exit(1);
		}
	}
	
	private boolean connect(String ip, int port) {
		try {
			this.ip = InetAddress.getByName(ip);
			this.port = port;
			socket = new DatagramSocket();
			
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			
			return false;
		} catch (SocketException e) {
			e.printStackTrace();
			
			return false;
		}
	}
	
	public String receive() {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String msg = new String(packet.getData());
		return msg;
	}
	
	public void send(final byte[] data) {
		new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void close() {
		socket.close();
	}
}
