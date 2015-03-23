package fr.veridiangames.main.network.server;

import java.net.InetAddress;

import org.lwjgl.util.vector.Vector3f;

public class ServerBullet {
	public int id, hid;
	public InetAddress ip;
	public int port;
	
	public Vector3f pos, dir;
	
	public ServerBullet(int id, int hid, InetAddress ip, int port) {
		this.id = id;
		this.hid = hid;
		this.port = port;
		this.ip = ip;
		this.pos = new Vector3f();
		this.dir = new Vector3f();
	}
	
	public String toString() {
		return id + "::" + hid + "::" + pos.x + "::" + pos.y + "::" + pos.z + "::" + dir.x + "::" + dir.y + "::" + dir.z;
	}
}	
