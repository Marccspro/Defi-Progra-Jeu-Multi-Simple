package fr.veridiangames.main.network.server;

import java.net.InetAddress;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class ServerClient {
	public int id;
	public String name;
	public InetAddress ip;
	public int port;
	
	public Vector3f pos;
	public Vector2f rot;
	public float life;
	
	public ServerClient(int id, String name, InetAddress ip, int port) {
		this.id = id;
		this.name = name;
		this.port = port;
		this.ip = ip;
		this.pos = new Vector3f();
		this.rot = new Vector2f();
		this.life = 1;
	}
	
	public String toString() {
		return id +"::" + name + "::" + life + "::" + pos.x + "::" + pos.y + "::" + pos.z + "::" + rot.x + "::" + rot.y;
	}
}	
