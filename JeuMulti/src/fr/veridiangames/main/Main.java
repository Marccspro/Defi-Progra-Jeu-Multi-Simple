package fr.veridiangames.main;

import static org.lwjgl.opengl.GL11.*;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

import fr.veridiangames.main.game.Game;

public class Main {
	public static void main(String[] args) {
		try {
			Display.setDisplayMode(new DisplayMode(720, 480));
			Display.setResizable(true);
			Display.create();
			
			glEnable(GL_TEXTURE_2D);
			glEnable(GL_DEPTH_TEST);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		Game game = new Game(JOptionPane.showInputDialog("Pseudo: "), "127.0.0.1", 2356);

		while (!Display.isCloseRequested()) {
			
			if (Mouse.isButtonDown(0) && !Mouse.isGrabbed()) Mouse.setGrabbed(true);
			if (Mouse.isGrabbed()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) Mouse.setGrabbed(false);
				game.update();				
			}
			game.updateServer();
			
			if (Display.wasResized()) {
				glViewport(0, 0, Display.getWidth(), Display.getHeight());
			}
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			GLU.gluPerspective(70.0f, (float) Display.getWidth() / (float) Display.getHeight(), 0.1f, 1000.0f);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();

			glRotatef(game.getLocalPlayer().rot.x, 1, 0, 0);
			glRotatef(game.getLocalPlayer().rot.y, 0, 1, 0);
			glTranslatef(-game.getLocalPlayer().pos.x, -game.getLocalPlayer().pos.y, -game.getLocalPlayer().pos.z);

			game.render();

			Display.sync(60);
			Display.update();
		}
		game.stop();

		Display.destroy();
		System.exit(0);
	}
}
