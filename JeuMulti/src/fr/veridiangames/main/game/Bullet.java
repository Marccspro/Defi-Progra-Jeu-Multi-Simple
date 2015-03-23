package fr.veridiangames.main.game;

import static org.lwjgl.opengl.GL11.*;

import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

public class Bullet {
	
	public int id;
	public Player holder;
	public Vector3f pos;
	public Vector3f dir;
	
	public Bullet(int id, Player holder, Vector3f pos, Vector3f dir) {
		this.id = id;
		this.holder = holder;
		this.pos = pos;
		this.dir = dir;
		
		pos.x += dir.x * 0.2f;
		pos.y += dir.y * 0.2f;
		pos.z += dir.z * 0.2f;
	}
	
	public Bullet(Player holder, Vector3f pos, Vector3f dir) {
		this.id = new Random().nextInt();
		this.holder = holder;
		this.pos = pos;
		this.dir = dir;
		
		pos.x += dir.x * 0.2f;
		pos.y += dir.y * 0.2f;
		pos.z += dir.z * 0.2f;
	}
	
	public void update() {
		pos.x += dir.x * 0.5f;
		pos.y += dir.y * 0.5f;
		pos.z += dir.z * 0.5f;
	}
	
	public int getHit(List<Player> players) {
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			float size = 0.30f;
			if (p.equals(holder))
				return -1;
			
			if (pos.x - size < p.pos.x && pos.x + size > p.pos.x &&
					pos.y - size < p.pos.y && pos.y + size > p.pos.y &&
					pos.z - size < p.pos.z && pos.z + size > p.pos.z) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void render() {
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, pos.z);
		
		glBegin(GL_QUADS);
			glColor4f(0.9f, 0.9f, 0.5f, 1);
			
			float size = 0.1f / 2f;
			float length = 0.35f / 4f;
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(-size, size, -size - length);
			
			glVertex3f(-size, -size, size - length);
			glVertex3f(size, -size, size - length);
			glVertex3f(size, size, size - length);
			glVertex3f(-size, size, size - length);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, -size, size - length);
			glVertex3f(-size, -size, size - length);
			
			glVertex3f(-size, size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size - length);
			glVertex3f(-size, size, size - length);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(-size, size, -size - length);
			glVertex3f(-size, size, size - length);
			glVertex3f(-size, -size, size - length);
			
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size - length);
			glVertex3f(size, -size, size - length);
		glEnd();
		
		glPopMatrix();
		glEnable(GL_TEXTURE_2D);
	}
	
	public String toString() {
		return id + "/-/" + holder.id + "/-/" + pos.x + "/-/" + pos.y + "/-/" + pos.z;
	}
}
