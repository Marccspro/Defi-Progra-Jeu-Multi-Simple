package fr.veridiangames.main.game;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import fr.veridiangames.main.game.level.Level;

public class Player {
	public int id;
	public String name;
	public Vector3f pos;
	public Vector2f rot;
	public float life;
	public boolean dead = false;
	public int deadAmnt = 0;
	public int killAmnt = 0;
	
	public Player(int id, String name, Vector3f pos) {
		this.id = id;
		this.name = name;
		this.pos = pos;
		this.rot = new Vector2f(0, 0);
	}
	
	public Player(int id, String name, Vector3f pos, Vector2f rot) {
		this.id = id;
		this.name = name;
		this.pos = pos;
		this.rot = rot;
	}
	
	int shootTime = 0;
	float xa, za;
	public void input(Game game, Level level) {
		shootTime++;
		float speed = 0.05f;
		
		rot.y += Mouse.getDX() * 0.5f;
		rot.x -= Mouse.getDY() * 0.5f;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			za += (float) (-speed * Math.cos(Math.toRadians(rot.y)));
			xa += (float) (speed * Math.sin(Math.toRadians(rot.y)));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			za += (float) (speed * Math.cos(Math.toRadians(rot.y)));
			xa += (float) (-speed * Math.sin(Math.toRadians(rot.y)));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			za += (float) (-speed * Math.cos(Math.toRadians(rot.y - 90)));
			xa += (float) (speed * Math.sin(Math.toRadians(rot.y - 90)));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			za += (float) (-speed * Math.cos(Math.toRadians(rot.y + 90)));
			xa += (float) (speed * Math.sin(Math.toRadians(rot.y + 90)));
		}
		
		if (!isColliding(xa, 0, level)) {
			pos.x += xa;			
		}
		if (!isColliding(0, za, level)) {
			pos.z += za;			
		}
		
		xa *= 0.5f;
		za *= 0.5f;
		
		if (Mouse.isButtonDown(0)) {
			if (shootTime % 10 == 1) {
				shoot(game);				
			}
		}else {
			shootTime = 0;
		}
	}
	
	public boolean isColliding(float xa, float za, Level level) {
		float size = 0.5f;
		
		int x0 = (int) (pos.x - size + xa);
		int x1 = (int) (pos.x + size + xa);
		int y0 = (int) (pos.z - size + za);
		int y1 = (int) (pos.z + size + za);
		
		if (level.getBlock(x0, y0).solid) return true;
		if (level.getBlock(x1, y0).solid) return true;
		if (level.getBlock(x1, y1).solid) return true;
		if (level.getBlock(x0, y1).solid) return true;
		
		return false;
	}
	
	public void giveDamage(float amnt) {
		System.out.println("DAMAGE: " + life + " " + amnt);
		life -= amnt;
		return;
	}
	
	public void addKill() {
		killAmnt++;
		return;
	}
	
	public void shoot(Game game) {
		Bullet bullet = new Bullet(this, new Vector3f(pos.x + 0.03f, pos.y - 0.1f, pos.z + 0.03f), getDirection());
		game.addBullet(bullet);
	}
	
	public Vector3f getDirection() {
		Vector3f r = new Vector3f();
		Vector2f rot = new Vector2f(this.rot.x, this.rot.y);
		
		float cosY = (float) Math.cos(Math.toRadians(rot.y - 90));
		float sinY = (float) Math.sin(Math.toRadians(rot.y - 90));
		float cosP = (float) Math.cos(Math.toRadians(-rot.x));
		float sinP = (float) Math.sin(Math.toRadians(-rot.x));
		
		r.x = cosY * cosP;
		r.y = sinP;
		r.z = sinY * cosP;
		
		return r;
	}
	
	public void update() {
		if (dead) {
			deadAmnt++;
			pos.x = 2;
			pos.z = 2;
			
			return;
		}
	}
	
	public void render() {
		glPushMatrix();
		
		glColor4f(0.5f, 0.5f, 0.5f, 1);	
		
		glTranslatef(pos.x, pos.y, pos.z);
		glRotatef(-rot.y, 0, 1, 0);
		glRotatef(-rot.x, 1, 0, 0);
		

		glBegin(GL_QUADS);
			float size = 0.2f;
			glVertex3f(-size, -size, -size);
			glVertex3f(size, -size, -size);
			glVertex3f(size, size, -size);
			glVertex3f(-size, size, -size);
			
			glVertex3f(-size, -size, size);
			glVertex3f(size, -size, size);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size);
			glVertex3f(size, -size, -size);
			glVertex3f(size, -size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(-size, size, -size);
			glVertex3f(size, size, -size);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size);
			glVertex3f(-size, size, -size);
			glVertex3f(-size, size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(size, -size, -size);
			glVertex3f(size, size, -size);
			glVertex3f(size, size, size);
			glVertex3f(size, -size, size);
			
			glColor4f(0.8f, 0.8f, 0.8f, 1);
			
			size = 0.1f;
			float length = 0.35f;
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(-size, size, -size - length);
			
			glVertex3f(-size, -size, size);
			glVertex3f(size, -size, size);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, -size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(-size, size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(-size, size, -size - length);
			glVertex3f(-size, size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size);
			glVertex3f(size, -size, size);
			
			glColor4f(0.7f, 0.7f, 0.7f, 1);
			size = 0.15f;
			length = 0.15f;
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(-size, size, -size - length);
			
			glVertex3f(-size, -size, size);
			glVertex3f(size, -size, size);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, -size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(-size, size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(-size, size, -size - length);
			glVertex3f(-size, size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size);
			glVertex3f(size, -size, size);
			
			glColor4f(0.2f, 0.2f, 0.2f, 1);
			size = 0.05f;
			length = 0.41f;
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(-size, size, -size - length);
			
			glVertex3f(-size, -size, size);
			glVertex3f(size, -size, size);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, -size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(-size, size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(-size, size, -size - length);
			glVertex3f(-size, size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size);
			glVertex3f(size, -size, size);
			
		glEnd();
		
		glPopMatrix();
	}
	
	public void renderLocal() {
		glPushMatrix();
		
		glColor4f(0.5f, 0.5f, 0.5f, 1);	
		
		glTranslatef(pos.x, pos.y, pos.z);
		
		glRotatef(-rot.y, 0, 1, 0);
		glRotatef(-rot.x, 1, 0, 0);
		
		glTranslatef(0, -0.1f, -0.2f);

		glBegin(GL_QUADS);
			glColor4f(0.5f, 0.5f, 0.5f, 1);
			
			float size = 0.1f / 4f;
			float length = 0.35f / 4f;
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(-size, size, -size - length);
			
			glVertex3f(-size, -size, size);
			glVertex3f(size, -size, size);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, -size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(-size, size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(-size, size, -size - length);
			glVertex3f(-size, size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size);
			glVertex3f(size, -size, size);
			
			glColor4f(0.7f, 0.7f, 0.7f, 1);
			size = 0.15f / 4f;
			length = -0.04f;
			glVertex3f(-size, -size, -size - length - length);
			glVertex3f(size, -size, -size - length - length);
			glVertex3f(size, size, -size - length - length);
			glVertex3f(-size, size, -size - length - length);
			
			glColor4f(0.6f, 0.6f, 0.6f, 1);
			glVertex3f(-size, -size, size - length);
			glVertex3f(size, -size, size - length);
			glVertex3f(size, size, size - length);
			glVertex3f(-size, size, size - length);
			
			glColor4f(0.7f, 0.7f, 0.7f, 1);
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, -size, size - length);
			glVertex3f(-size, -size, size - length);
			
			glVertex3f(-size, size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size - length);
			glVertex3f(-size, size, size - length);
			glColor4f(0.65f, 0.65f, 0.65f, 1);
			glVertex3f(-size, -size, -size - length);
			glVertex3f(-size, size, -size - length);
			glVertex3f(-size, size, size - length);
			glVertex3f(-size, -size, size - length);
			glColor4f(0.7f, 0.7f, 0.7f, 1);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size - length);
			glVertex3f(size, -size, size - length);
			
			glColor4f(0.2f, 0.2f, 0.2f, 1);
			size = 0.05f / 4f;
			length = 0.41f / 4f;
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(-size, size, -size - length);
			
			glVertex3f(-size, -size, size);
			glVertex3f(size, -size, size);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, -size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(-size, size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size);
			glVertex3f(-size, size, size);
			
			glVertex3f(-size, -size, -size - length);
			glVertex3f(-size, size, -size - length);
			glVertex3f(-size, size, size);
			glVertex3f(-size, -size, size);
			
			glVertex3f(size, -size, -size - length);
			glVertex3f(size, size, -size - length);
			glVertex3f(size, size, size);
			glVertex3f(size, -size, size);
			
		glEnd();
		
		glPopMatrix();
	}
	
	public String toString() {
		return id +"/-/" + name + "/-/" + life + "/-/" + pos.x + "/-/" + pos.y + "/-/" + pos.z + "/-/" + rot.x + "/-/" + rot.y;
	}
}
