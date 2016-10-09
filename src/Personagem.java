import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Personagem extends Sprite {
	
	BufferedImage img;
	
	int frametimer = 0;
	int frame = 0;
	int intervalo = 250;
	int anim = 0;
	
	int charx = 0;
	int chary = 0;
	int charw = 72;
    int charh = 128;
    
    int vel = 100;
    
    boolean LEFT, RIGHT,UP,DOWN;

	public Personagem(BufferedImage img,int x,int y, int character) {
		// TODO Auto-generated constructor stub
		this.x = x;
		this.y = y;
		this.img = img;
		
		charx = character%4;
		chary = character/4;
	}
	
	@Override
	public void simulaSe(long DiffTime) {
		// TODO Auto-generated method stub
		frametimer+=DiffTime;
		frame = (frametimer/intervalo)%3;
		
		float oldx = x;
		float oldy = y;
		
		if(LEFT){
			x -= vel*DiffTime/1000.0f;
			anim = 3;
		}
		if(RIGHT){
			x += vel*DiffTime/1000.0f;
			anim = 1;
		}
		if(UP){
			y -= vel*DiffTime/1000.0f;
			anim = 0;
		}
		if(DOWN){
			y += vel*DiffTime/1000.0f;
			anim = 2;
		}
		
		int bx = (int)((x+12)/GamePanel.mapa.tileW);
		int by = (int)((y+28)/GamePanel.mapa.tileH);
		
		//System.out.println(" bx "+bx+" by "+by);
		
		if(bx<0||by<0||bx>=GamePanel.mapa.Largura||by>=GamePanel.mapa.Altura||GamePanel.mapa.mapa2[by][bx]!=0){
			x = oldx;
			y = oldy;
		}
		
		if((x>GamePanel.mapa.Largura*GamePanel.mapa.tileW-24) || x <= 0){
			x = oldx;
			y = oldy;
		}
		if((y>GamePanel.mapa.Altura*GamePanel.mapa.tileH-32) || y <= 0){
			x = oldx;
			y = oldy;
		}
	}

	@Override
	public void desenhaSe(Graphics2D dbg,int telax,int telay){
		// TODO Auto-generated method stub
		dbg.drawImage(img, (int)x-telax,(int)y-telay,(int)x+24-telax,(int)y+32-telay,charx*charw+frame*24,chary*charh+anim*32,charx*charw+frame*24+24,chary*charh+anim*32+32,null);
	}

}
