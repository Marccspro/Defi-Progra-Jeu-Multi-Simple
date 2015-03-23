package fr.veridiangames.main.game;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import fr.veridiangames.main.game.level.Level;
import fr.veridiangames.main.network.Client;

public class Game implements Runnable {
	
	private List<Bullet> bullets = new ArrayList<Bullet>();
	private List<Player> players = new ArrayList<Player>();
	
	private Client client;
	private boolean running = false;

	Level level;
	
	public Game(String name, String ip, int port) {
		client = new Client(name, ip, port);
		int x = 8;
		int y = 10; 
		
		String connection = "-c/-/" + client.id + "/-/" + client.name + "/-/" + x + "/-/" + y;
		client.send(connection.getBytes());
		
		Player p = new Player(client.id, client.name, new Vector3f(x, 0.6f, y));
		players.add(p);
		
		running = true;
		new Thread(this, "Game Client").start();
		
		level = new Level("map");
	}
	
	public void update() {
		players.get(0).input(this, level);
	}
	
	public void updateServer() {
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update();
			int index = bullets.get(i).getHit(players);
			if (index != -1 && index != 0) {
				Player p = players.get(index);
				p.dead = true;
				bullets.get(i).holder.addKill();
				continue;
			}
		}
		
		for (int i = 0; i < players.size(); i++) {
			players.get(i).update();
		}
		
		String msg = "-u/-/" + players.get(0).toString();
		client.send(msg.getBytes());
	}
	
	public void render() {
		level.render();
		for (int i = 0; i < players.size(); i++) {
			if (i != 0) {
				players.get(i).render();				
			}else {
				players.get(i).renderLocal();
			}
		}
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render();
		}
	}
	
	public void run() {
		while(running) {
			String msg = client.receive().trim();
			String[] values = msg.split("/-/");
			String prefix = values[0];
			
			if (prefix.equals("-c")) {
				String[] token = values[1].split("::");
				int id = Integer.parseInt(token[0]);
				String name = token[1];
				
				if (client.id == id) 
					continue;
				
				float x = Float.parseFloat(token[3]);
				float y = Float.parseFloat(token[4]);
				float z = Float.parseFloat(token[5]);
				
				System.out.println(name + " joined the game !");
				
				addPlayer(id, name, x, y, z);
			}else if (prefix.equals("-d")) {
				int id = Integer.parseInt(values[1]);
				String name = values[2];
				removePlayer(id);
				System.out.println(name + " disconnected !");
			}else if (prefix.equals("-sync")) {
				String text = msg.substring(8);
				if (text.equals("-empty-"))
					continue;
				String[] users = text.split("/-/");
				for (int i = 0; i < users.length; i++) {
					String[] token = users[i].split("::");
					int id = Integer.parseInt(token[0]);
					String name = token[1];
					float life = Float.parseFloat(token[2]);
					
					float xp = Float.parseFloat(token[3]);
					float yp = Float.parseFloat(token[4]);
					float zp = Float.parseFloat(token[5]);
					
					float xr = Float.parseFloat(token[6]);
					float yr = Float.parseFloat(token[7]);
					
					Player p = new Player(id, name, new Vector3f(xp, yp, zp), new Vector2f(xr, yr));
					p.life = life;
					
					players.add(p);
				}
			}else if (prefix.equals("-u")) {
				String text = values[1];
				String[] token = text.split("::");
				int id = Integer.parseInt(token[0]);
				float life = Float.parseFloat(token[2]);
				
				float xp = Float.parseFloat(token[3]);
				float yp = Float.parseFloat(token[4]);
				float zp = Float.parseFloat(token[5]);
				
				float xr = Float.parseFloat(token[6]);
				float yr = Float.parseFloat(token[7]);
				
				Player p = getPlayer(id);
				if (p != null) {
					p.life = life;
					p.pos = new Vector3f(xp, yp, zp);
					p.rot = new Vector2f(xr, yr);
				}
			}else if (prefix.equals("-ab")) {
				String text = values[1];
				String[] v = text.split("::");
				int id = Integer.parseInt(v[0]);
				int holderID = Integer.parseInt(v[1]);
				
				if (client.id == holderID) {
					continue;
				}
				
				float xp = Float.parseFloat(v[2]);
				float yp = Float.parseFloat(v[3]);
				float zp = Float.parseFloat(v[4]);
				
				float xd = Float.parseFloat(v[5]);
				float yd = Float.parseFloat(v[6]);
				float zd = Float.parseFloat(v[7]);
				
				Bullet bullet = new Bullet(id, getPlayer(holderID), new Vector3f(xp, yp, zp), new Vector3f(xd, yd, zd));
				bullets.add(bullet);
			}else if (prefix.equals("-ub")) {
				String text = values[1];
				String[] v = text.split("::");
				
				int id = Integer.parseInt(v[0]);
				int holderID = Integer.parseInt(v[1]);
				if (client.id == holderID) {
					continue;
				}
				float xp = Float.parseFloat(v[2]);
				float yp = Float.parseFloat(v[3]);
				float zp = Float.parseFloat(v[4]);
				
				int bullet = getBulletIndex(id);
				if (bullet != 0) {
					bullets.get(bullet).pos = new Vector3f(xp, yp, zp);					
				}
			}else {
				System.out.println("MSG: " + msg + " " + prefix);
			}
		}
	}
	
	public void addPlayer(int id, String name, float x, float y, float z) {
		Player p = new Player(id, name, new Vector3f(x, y, z));
		players.add(p);
	}
	
	public void removePlayer(int id) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).id == id) {
				players.remove(i);
				break;
			}
		}
	}
	
	public Player getPlayer(int id) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).id == id) {
				return players.get(i);
			}
		}
		
		return null;
	}
	
	public void addBullet(Bullet bullet) {
		bullets.add(bullet);
		client.send(new String("-ab/-/" + bullet.id + "/-/" + bullet.holder.id + "/-/" + bullet.pos.x + "/-/" + bullet.pos.y + "/-/" + bullet.pos.z + "/-/" + bullet.dir.x + "/-/" + bullet.dir.y + "/-/" + bullet.dir.z).getBytes());
	}
	
	public Bullet getBullet(int id) {
		for (int i = 0; i < bullets.size(); i++) {
			if (bullets.get(i).id == id)
				return bullets.get(i);
		}
		
		return null;
	}
	
	public int getBulletIndex(int id) {
		for (int i = 0; i < bullets.size(); i++) {
			if (bullets.get(i).id == id)
				return i;
		}
		
		return 0;
	}
	
	public Player getLocalPlayer() {
		return players.get(0);
	}
	
	public void stop() {
		String msg = "-d/-/" + client.id;
		client.send(msg.getBytes());
	}
}
