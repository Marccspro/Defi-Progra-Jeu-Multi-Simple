package fr.veridiangames.main.game.level.blocks;

public class Block {
	public  int x, y;
	public boolean solid = false;
	public boolean removed = false;
	
	public Block(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
