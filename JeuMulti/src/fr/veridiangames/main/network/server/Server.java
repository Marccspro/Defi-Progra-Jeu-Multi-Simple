package fr.veridiangames.main.network.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Server implements Runnable {
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	private List<ServerBullet> bullets = new ArrayList<ServerBullet>();
	
	private DatagramSocket socket;
	private int port;
	
	private boolean running = false;
	private Scanner sc;
	
	public Server(int port, Scanner sc) {
		this.port = port;
		this.sc = sc;
		
		System.out.println("Starting Server...");
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.err.println("Server did not start due to a problem !");
			System.err.println("Terminating...");
			System.exit(1);
		}
		
		running = true;
		new Thread(this, "Server").start();
	}
	
	public void run() {
		System.out.println("Server started on port " + port);
		receive();
		
		while(running) {
			String msg = sc.nextLine();
			if (msg.equals("stop")) {
				stop();
			}
		}
		sc.close();
	}
	
	private void receive() {
		new Thread("Receive") {
			public void run() {
				while(running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					parsePacket(packet);
				}
			}
		}.start();
	}
	
	private void parsePacket(DatagramPacket packet) {
		String msg = new String(packet.getData()).trim();
		String[] values = msg.split("/-/");
		String prefix = values[0];
		
		if (prefix.equals("-c")) {
			int id = Integer.parseInt(values[1]);
			String name = values[2];
			float xp = Float.parseFloat(values[3]);
			float zp = Float.parseFloat(values[4]);
			
			ServerClient client = new ServerClient(id, name, packet.getAddress(), packet.getPort());
			client.life = 1;
			client.pos = new Vector3f(xp, 0, zp);
			client.rot = new Vector2f(0, 0);
			
			String clientList = "-sync/-/" + getClients();
			clients.add(client);
			
			String con = "-c/-/" + client.toString();
			sendToAll(con);
			send(clientList.getBytes(), packet.getAddress(), packet.getPort());
			System.out.println(name + " joined the game !");
		}else if (prefix.equals("-d")) {
			int id = Integer.parseInt(values[1]);
			disconnect(id);
		}else if (prefix.equals("-u")) {
			int id = Integer.parseInt(values[1]);
			float life = Float.parseFloat(values[3]);
			float xp = Float.parseFloat(values[4]);
			float yp = Float.parseFloat(values[5]);
			float zp = Float.parseFloat(values[6]);
			
			float xr = Float.parseFloat(values[7]);
			float yr = Float.parseFloat(values[8]);
			
			ServerClient sc = getClient(id);
			
			if (sc != null) {
				sc.life = life;
				sc.pos = new Vector3f(xp, yp, zp);
				sc.rot = new Vector2f(xr, yr);
			}
			
			String update = "-u/-/" + sc.toString();
			sendToAll(update);
		}else if(prefix.equals("-ab")) {
			int id = Integer.parseInt(values[1]);
			int holderID = Integer.parseInt(values[2]);
			float xp = Float.parseFloat(values[3]);
			float yp = Float.parseFloat(values[4]);
			float zp = Float.parseFloat(values[5]);
			
			float xd = Float.parseFloat(values[6]);
			float yd = Float.parseFloat(values[7]);
			float zd = Float.parseFloat(values[8]);
			
			ServerBullet bullet = new ServerBullet(id, holderID, packet.getAddress(), packet.getPort());
			bullet.pos = new Vector3f(xp, yp, zp);
			bullet.dir = new Vector3f(xd, yd, zd);
			
			bullets.add(bullet);
			
			sendToAll("-ab/-/" + bullet.toString());
		}else {
			System.out.println("MSG: " + msg + " |||| " + prefix);
		}
	}
	
	private void disconnect(int id) {
		ServerClient client = getClient(id);
		if (client != null) {
			sendToAll("-d/-/" + client.id + "/-/" + client.name);
			clients.remove(client);
			System.out.println(client.name + " disconnected !");
		}
	}
	
	private ServerClient getClient(int id) {
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).id == id) {
				return clients.get(i);
			}
		}
		return null;
	}
	
	private ServerBullet getBullet(int id) {
		for (int i = 0; i < bullets.size(); i++) {
			if (bullets.get(i).id == id) {
				return bullets.get(i);
			}
		}
		return null;
	}
	
	private void sendToAll(String msg) {
		for (int i = 0; i < clients.size(); i++) {
			ServerClient c = clients.get(i);
			send(msg.getBytes(), c.ip, c.port);
		}
	}
	
	private void send(final byte[] data, final InetAddress ip, final int port) {
		new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
				try {
					socket.send(packet);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private String getClients() {
		String users = "";
		if (clients.isEmpty()) 
			return "-empty-";
		
		for (int i = 0; i < clients.size(); i++) {
			users += clients.get(i).toString() + "/-/";
		}
		
		return users;
	}
	
	private void stop() {
		running = false;
		socket.close();
		
		System.out.println("Server stopped !");
		System.exit(0);
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Préciser le port: ");
		int port = sc.nextInt();
		
		new Server(port, sc);
	}
}
