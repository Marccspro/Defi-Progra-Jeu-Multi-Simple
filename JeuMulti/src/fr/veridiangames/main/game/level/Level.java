package fr.veridiangames.main.game.level;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import fr.veridiangames.main.game.level.blocks.Block;
import fr.veridiangames.main.game.level.blocks.SolidBlock;
import fr.veridiangames.main.rendering.Renderer;
import fr.veridiangames.main.rendering.Texture;

public class Level {
	
	public int width, height;
	private int list;
	
	public Block[] blocks;
	
	public Level(String level) {
		compile(level);
	}
	
	public void compile(String level) {
		BufferedImage map = null;
		try {
			map = ImageIO.read(Level.class.getResourceAsStream("/textures/" + level + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int w = width = map.getWidth();
		int h = height = map.getHeight();
		
		int[] pixels = new int[w * h];
		map.getRGB(0, 0, w, h, pixels, 0, w);
		
		blocks = new Block[w * h];
		
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int i = x + y * w;
				
				if (pixels[i] == 0xFF000000)
					blocks[i] = new Block(x, y);
				
				if (pixels[i] == 0xFFFFFFFF)
					blocks[i] = new SolidBlock(x, y);
			}	
		}
		
		
		compileRendering();
	}
	
	public void compileRendering() {
		list = glGenLists(1);
		glNewList(list, GL_COMPILE);
		glBegin(GL_QUADS);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Block block = getBlock(x, y);
				
				if (!block.solid) {
					Renderer.setFloorData(x, y, 1);
					Renderer.setCeilingData(x, y, 2);
				}
				
				Block left = getBlock(x + 1, y);
				Block down = getBlock(x, y + 1);
				
				if (block.solid) {
					if (!left.solid) {
						Renderer.setWallData(x + 1, y + 1, x + 1, y, 0);
					}
					if (!down.solid) {
						Renderer.setWallData(x, y + 1, x + 1, y + 1, 0);
					}
				}else {
					if (left.solid) {
						Renderer.setWallData(x + 1, y, x + 1, y + 1, 0);
					}
					if (down.solid) {
						Renderer.setWallData(x + 1, y + 1, x, y + 1, 0);
					}
				}
			}	
		}
		
		glEnd();
		glEndList();
	}
	
	public Block getBlock(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return new SolidBlock(x, y);
		}
		return blocks[x + y * width];
	}
	
	public void render() {
		Texture.env.bind();
		glCallList(list);
	}
}
