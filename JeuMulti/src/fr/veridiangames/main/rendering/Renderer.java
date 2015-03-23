package fr.veridiangames.main.rendering;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
	
	private static float width = 4f;
	private static float height = 4f;
	
	public static void setFloorData(float x, float z, int tex) {
		float xo = tex % (int) width;
		float yo = tex / (int) height;
		
		glColor4f(1, 1, 1, 1);
		
		glTexCoord2f((0 + xo) / width, (0 + yo) / height); glVertex3f(x + 1, 0, z);
		glTexCoord2f((1 + xo) / width, (0 + yo) / height); glVertex3f(x, 0, z);
		glTexCoord2f((1 + xo) / width, (1 + yo) / height); glVertex3f(x, 0, z + 1);
		glTexCoord2f((0 + xo) / width, (1 + yo) / height); glVertex3f(x + 1, 0, z + 1);
	}
	
	public static void setCeilingData(float x, float z, int tex) {
		float xo = tex % (int) width;
		float yo = tex / (int) height;
		
		glColor4f(1, 1, 1, 1);
		
		glTexCoord2f((0 + xo) / width, (0 + yo) / height); glVertex3f(x, 1, z);
		glTexCoord2f((1 + xo) / width, (0 + yo) / height); glVertex3f(x + 1, 1, z);
		glTexCoord2f((1 + xo) / width, (1 + yo) / height); glVertex3f(x + 1, 1, z + 1);
		glTexCoord2f((0 + xo) / width, (1 + yo) / height); glVertex3f(x, 1, z + 1);
	}
	
	public static void setWallData(float x0, float z0, float x1, float z1, int tex) {
		float xo = tex % (int) width;
		float yo = tex / (int) height;
		
		glColor4f(1, 1, 1, 1);
		
		glTexCoord2f((0 + xo) / width, (0 + yo) / height); glVertex3f(x0, 0, z0);
		glTexCoord2f((1 + xo) / width, (0 + yo) / height); glVertex3f(x1, 0, z1);
		glTexCoord2f((1 + xo) / width, (1 + yo) / height); glVertex3f(x1, 1, z1);
		glTexCoord2f((0 + xo) / width, (1 + yo) / height); glVertex3f(x0, 1, z0);
	}
}
