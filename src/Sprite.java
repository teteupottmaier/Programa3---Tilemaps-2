import java.awt.Graphics2D;

public abstract class Sprite {
	float x,y;
	
	abstract public void simulaSe(long DiffTime);
	abstract public void desenhaSe(Graphics2D dbg,int telax,int telay);
}
