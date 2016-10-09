import java.awt.image.BufferedImage;

public class Inimigo extends Personagem {

	int velx;
	int vely;
	
	public Inimigo(BufferedImage img, int x, int y, int character, int velx,int vely) {
		super(img, x, y, character);
		this.velx = velx;
		this.vely = vely;
	}
	
	@Override
	public void simulaSe(long DiffTime) {
		// TODO Auto-generated method stub
		super.simulaSe(DiffTime);
		
		x+=velx*DiffTime/1000.0;
		y+=vely*DiffTime/1000.0;
		
		if(Math.abs(velx)>Math.abs(vely)){
			if(velx>0){
				anim = 1;
			}else{
				anim = 3;
			}
		}else{
			if(vely<0){
				anim = 0;
			}else{
				anim = 2;
			}
		}
		
		if((x>GamePanel.mapa.Largura*GamePanel.mapa.tileW-24) || x <= 0){
			velx =-velx;
		}
		if((y>GamePanel.mapa.Altura*GamePanel.mapa.tileH-32) || y <= 0){
			vely =-vely;
		}
		
	}

}
