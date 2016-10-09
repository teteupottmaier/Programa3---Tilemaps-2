import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.*;
import javax.imageio.ImageIO;


public class GamePanel extends JPanel implements Runnable
{
public static final int PWIDTH = 640;
public static final int PHEIGHT = 480;
private Thread animator;
private boolean running = false;
private boolean gameOver = false; 

private BufferedImage dbImage;
private Graphics2D dbg;


int FPS,SFPS;
int fpscount;

Random rnd = new Random();

BufferedImage imagemcharsets;
BufferedImage fundo;
BufferedImage plotfundo;
BufferedImage tileset;


boolean LEFT, RIGHT,UP,DOWN;

int MouseX,MouseY;

int diftime;

float x,y;
float x2,y2;



int vel2 = 80;

int objetivoX = 0;
int objetivoY = 0;

Personagem heroi;
ArrayList<Sprite> listaDePersonagens = new ArrayList<Sprite>();

public static TileMapJSON mapa;

public GamePanel()
{

	setBackground(Color.white);
	setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

	// create game components
	setFocusable(true);

	requestFocus(); // JPanel now receives key events
	
	if (dbImage == null){
		dbImage = new BufferedImage(PWIDTH, PHEIGHT,BufferedImage.TYPE_4BYTE_ABGR);
		if (dbImage == null) {
			System.out.println("dbImage is null");
			return;
		}else{
			dbg = (Graphics2D)dbImage.getGraphics();
		}
	}	
	
	
	// Adiciona um Key Listner
	addKeyListener( new KeyAdapter() {
		public void keyPressed(KeyEvent e)
			{ 
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_LEFT){
					LEFT = true;
				}
				if(keyCode == KeyEvent.VK_RIGHT){
					RIGHT = true;
				}
				if(keyCode == KeyEvent.VK_UP){
					UP = true;
				}
				if(keyCode == KeyEvent.VK_DOWN){
					DOWN = true;
				}	
			}
		@Override
			public void keyReleased(KeyEvent e ) {
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_LEFT){
					LEFT = false;
				}
				if(keyCode == KeyEvent.VK_RIGHT){
					RIGHT = false;
				}
				if(keyCode == KeyEvent.VK_UP){
					UP = false;
				}
				if(keyCode == KeyEvent.VK_DOWN){
					DOWN = false;
				}
			}
	});
	
	addMouseMotionListener(new MouseMotionListener() {
		
		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			MouseX = e.getX();
			MouseY = e.getY();
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			MouseX = e.getX();
			MouseY = e.getY();
		}
	});
	
	addMouseListener(new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			objetivoX = MouseX;
			objetivoY = MouseY;
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	});
	
	

	fundo =  loadImage("wide_r.jpg");
	plotfundo = new BufferedImage(fundo.getWidth(), fundo.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
	tileset = loadImage("Bridge.png");
	
	
	imagemcharsets = loadImage("chara1b.png");
	DataBufferByte db = (DataBufferByte)imagemcharsets.getRaster().getDataBuffer();
	byte data[] = db.getData();
	
	int ab = data[1];
	int ag = data[2];
	int ar = data[3];
	
	for(int i = 0; i < data.length; i+=4){
		int a = data[i]&0x00ff;
		int b = data[i+1]&0x00ff;
		int g = data[i+2]&0x00ff;
		int r = data[i+3]&0x00ff;
		
//		int media = (r+g+b)/3;
//		
//		r = media;
//		g = media;
//		b = media;
		
		//255,0,128

//		b = Math.min(255,(int)(b*1.5));
//		g = Math.min(255,(int)(g*1.5));
//		r = Math.min(255,(int)(r*0.75));
		
		if(ab==b&&ag==g&&ar==r){
			data[i] = 0;
		}
		
		data[i+1] = (byte)(b&0x00ff);
		data[i+2] = (byte)(g&0x00ff);
		data[i+3] = (byte)(r&0x00ff);
	}
	

	
	heroi = new Personagem(imagemcharsets, 10, 10,0);
	
	MouseX = MouseY = 0;
	
	mapa = new TileMapJSON(tileset,40,30);
	mapa.AbreMapa("mapa1.json");
	
	for(int i = 0;i < 10;i++){
		Inimigo inimigotmp = new Inimigo(imagemcharsets, rnd.nextInt(600), rnd.nextInt(440), rnd.nextInt(7)+1, rnd.nextInt(200)-100, rnd.nextInt(200)-100);
		listaDePersonagens.add(inimigotmp);
	}
	x = 10;
	y = 100;
	x2 = 10;
	y2 = 200;

} // end of GamePanel()
BufferedImage loadImage(String filename){
	try {
		BufferedImage tmp = ImageIO.read( getClass().getResource(filename) );
		BufferedImage imgfinal = new BufferedImage(tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		imgfinal.getGraphics().drawImage(tmp, 0, 0, null);
		return imgfinal;
	}
	catch(IOException e) {
		System.out.println("Load Image error:");
	}
	return null;
}

public void addNotify()
{
	super.addNotify(); // creates the peer
	startGame(); // start the thread
}

private void startGame()
// initialise and start the thread
{
	if (animator == null || !running) {
		animator = new Thread(this);
		animator.start();
	}
} // end of startGame()

public void stopGame()
// called by the user to stop execution
{ running = false; }


public void run()
/* Repeatedly update, render, sleep */
{
	running = true;
	
	long DifTime,TempoAnterior;
	
	int segundo = 0;
	DifTime = 0;
	TempoAnterior = System.currentTimeMillis();
	
	while(running) {
	
		gameUpdate(DifTime); // game state is updated
		gameRender(); // render to a buffer
		paintImmediately(0, 0, 640, 480); // paint with the buffer
	
		try {
			Thread.sleep(0); // sleep a bit
		}	
		catch(InterruptedException ex){}
		
		DifTime = System.currentTimeMillis() - TempoAnterior;
		TempoAnterior = System.currentTimeMillis();
		
		if(segundo!=((int)(TempoAnterior/1000))){
			FPS = SFPS;
			SFPS = 1;
			segundo = ((int)(TempoAnterior/1000));
		}else{
			SFPS++;
		}
	
	}
System.exit(0); // so enclosing JFrame/JApplet exits
} // end of run()

int timerfps = 0;

int passo = 0;

private void gameUpdate(long DiffTime)
{ 
	diftime = (int)DiffTime;
	passo+=DiffTime;
	
	heroi.LEFT = LEFT;
	heroi.RIGHT = RIGHT;
	heroi.UP = UP;
	heroi.DOWN = DOWN;
	
	heroi.simulaSe(DiffTime);
	
	for(int i = 0; i < listaDePersonagens.size();i++){
		listaDePersonagens.get(i).simulaSe(DiffTime);
	}

	mapa.Posiciona((int)(heroi.x-PWIDTH/2),(int)(heroi.y-PHEIGHT/2));
}

private void gameRender()
// draw the current frame to an image buffer
{
	dbg.setColor(Color.white);
	dbg.fillRect(0, 0, PWIDTH, PHEIGHT);
	
	mapa.DesenhaSe(dbg);
	
	//dbg.drawImage(fundo, 0, 0, null);
	
//	int imgw = fundo.getWidth();
//	int imgh = fundo.getHeight();
//	
//	float prop = imgw/(float)imgh;
//	
//	int neww = (int)(PHEIGHT*prop);
//	int newh = (int)PHEIGHT;
//
//	if(neww>PWIDTH){
//		neww = PWIDTH;
//		newh = (int)(PWIDTH/prop);
//	}
	
	heroi.desenhaSe(dbg,mapa.MapX,mapa.MapY);
	
	for(int i = 0; i < listaDePersonagens.size();i++){
		listaDePersonagens.get(i).desenhaSe(dbg,mapa.MapX,mapa.MapY);
	}
	
	
	//dbg.drawImage(plotfundo, 0, 0, neww, newh, null);

	//dbg.drawImage(fundo, 100, 100, 540, 380, 1876, 10, 2755, 633, null);

	//dbg.drawImage(fundo, MouseX, MouseY, MouseX+100, MouseY+100, MouseX, MouseY, (MouseX+100), (MouseY+100), null);
	
	
	dbg.setColor(Color.blue);
	dbg.drawString("FPS "+FPS+ " "+MouseX+" "+MouseY+" LEFT "+LEFT+" RIGHT "+RIGHT, 10, 20);
}


public void paintComponent(Graphics g)
{
	super.paintComponent(g);
	if (dbImage != null)
		g.drawImage(dbImage, 0, 0, null);
}


public static void main(String args[])
{
	GamePanel ttPanel = new GamePanel();

  // create a JFrame to hold the timer test JPanel
  JFrame app = new JFrame("Swing Timer Test");
  app.getContentPane().add(ttPanel, BorderLayout.CENTER);
  app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  app.pack();
  app.setResizable(false);  
  app.setVisible(true);
} // end of main()

} // end of GamePanel class

