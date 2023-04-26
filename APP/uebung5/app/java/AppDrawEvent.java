import java.awt.*;
import java.awt.image.BufferedImage; 
import java.awt.event.*; 
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.lang.Math;

/**Appframe gegeben */
class AppFrame extends JFrame { 
    	public AppFrame(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } 
}
/** hauptklasse*/
public class AppDrawEvent{
	/**setnum = anzahl an wiederholungen */
	public static volatile int setnum = 0;
	/**ob das threat weiterlaufen darf */
	public static volatile boolean running = false;

	/**arbeiter thread ruf alles */
	static class arbeiter extends Thread{
		BufferedImage currentimg;
		BufferedImage newimg;
		
		public arbeiter(BufferedImage a)
		{
			currentimg = a;
			start();
		}

		public void run(){
			int i = 0;
			reload(currentimg);

			while(setnum == 0){
				try{Thread.sleep(300);}catch(InterruptedException e){System.out.println(e);}
			}

			while(true){
				try{Thread.sleep(300);}catch(InterruptedException e){System.out.println(e);}
				if(running){
					for(;i < setnum+1; i++){
						if(i == setnum){
							reload(currentimg);
							running = false;
							i = i + 1;
						}
						currentimg = nextimg(currentimg);
					}
					i = i - 1;
				}
			}
		}	
	}
	/**Das neu laden des Frames */
	public static void reload(BufferedImage img){
		JFrame frame = new AppFrame("Output");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		JPanel top = new JPanel(new FlowLayout());
		JPanel middle = new JPanel(new FlowLayout());
		JPanel bottom = new JPanel(new FlowLayout());
		JTextField inputnum = new JTextField();
		inputnum.setPreferredSize(new Dimension(75, 20) );
		JButton b1 = new JButton("Start");
		JButton b2 = new JButton("Exit");
		b2.addActionListener((event) -> System.exit(0));

		b1.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent arg0){
			int i; 
			try{
				setnum = Integer.parseInt(inputnum.getText());
				running = true;
			}
			catch(Exception e){
				i = 0;
			}
		}	
		});
	
		JLabel l1 = new JLabel();
		l1 = new JLabel(new ImageIcon(img));
		JLabel l2 = new JLabel("Rounds:");

		top.add(l2);
		top.add(inputnum);
		top.add(b1);

		middle.add(l1);

		bottom.add(b2);

		panel.add(top);
		panel.add(middle);
		panel.add(bottom);
		
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	/**bestimmen des nächsten Bildes auf basis des alten*/
	public static BufferedImage nextimg(BufferedImage img){
		BufferedImage newimg = new BufferedImage(img.getWidth(),img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for(int n1 = 0; n1 < newimg.getWidth(); n1 = n1 + 1){
			for(int n2 = 0; n2 < newimg.getHeight(); n2 = n2 + 1){
				Color col = new Color(img.getRGB(n1,n2),true);
				newimg.setRGB(n1,n2,col.getRGB());
			}
		}

		
		for(int n1 = 0; n1 < newimg.getWidth(); n1 = n1 + 1){
			for(int n2 = 0; n2 < newimg.getHeight(); n2 = n2 + 1){
				int counter = 0;
				int cordn1 = 0;
				int cordn2 = 0;

				for(int x = -1; x < 2; x++){
					for(int y = -1; y < 2; y++){
						cordn1 = n1 + x;
						cordn2 = n2 + y;
						
						if(cordn1 < 0){
							cordn1 = newimg.getWidth()-1;
						}

						if(cordn2 < 0){
							cordn2 = newimg.getHeight()-1;
						}

						if(cordn1 > newimg.getWidth()-1){
							cordn1 = 0;
						}

						if(cordn2 > newimg.getHeight()-1){
							cordn2 = 0;
						}
			
						if(img.getRGB(cordn1,cordn2) != -1 && !((cordn1 == n1) && (cordn2 == n2))){
							counter = counter + 1;
							}
						}
					}
				if(counter % 2 == 0){
					newimg.setRGB(n1,n2,new Color(255,255,255).getRGB() );
				}
				else{
					newimg.setRGB(n1,n2,new Color(0,0,0).getRGB() );
				}
					
				}
			}
		return newimg;
	}

/**main methode einlesen und umwandeln des Bildes Umwandeln des Bildes in die GreyPatternform und setzen der größe des Frames */
	public static void main( String[] args ) {
		int width = 32;
		int height = 32;

		BufferedImage image = null;
		
		try {
			File inputfile = new File("smile32.jpg");

			image = new BufferedImage(width*8,height*8, BufferedImage.TYPE_INT_ARGB);
		
			image = ImageIO.read(inputfile);
		}
		catch(IOException e){
			System.out.println("Error");
		}

		int border = 9;
		BufferedImage writtenimage = new BufferedImage(width*2*border,height*2*border, BufferedImage.TYPE_INT_ARGB);
		
		for(int n1 = 0; n1 < writtenimage.getWidth(); n1 = n1 + 1){
			for(int n2 = 0; n2 < writtenimage.getHeight(); n2 = n2 + 1){
				writtenimage.setRGB(n1,n2,new Color(255, 255, 255).getRGB());
			}
		}

		for(int n1 = 0; n1 < image.getWidth(); n1 = n1 + 1){
			for(int n2 = 0; n2 < image.getHeight(); n2 = n2 + 1){
				Color col = new Color(0,0,0);
				col = new Color(image.getRGB(n1,n2),true);
				int rgb = (col.getRed() + col.getBlue() + col.getGreen()) / 3;
				int five = rgb / 52;

				Color color = new Color(255,255,255);
				Color color2 = new Color(0,0,0);

				int addheight = (int)((border-1) * height);
				int addwidth = (int)((border-1) * width);

				if(five == 4){
					writtenimage.setRGB(n1*2 + addwidth, n2*2 + addheight,color.getRGB());
					writtenimage.setRGB(n1*2+1 + addwidth, n2*2 + addheight,color.getRGB());
					writtenimage.setRGB(n1*2 + addwidth, n2*2+1 + addheight,color.getRGB());
					writtenimage.setRGB(n1*2+1 + addwidth, n2*2+1 + addheight,color.getRGB());
				}
				if(five == 3){
					writtenimage.setRGB(n1*2 + addwidth,n2*2 + addheight,color2.getRGB());
					writtenimage.setRGB(n1*2+1 + addwidth,n2*2 + addheight,color.getRGB());
					writtenimage.setRGB(n1*2 + addwidth,n2*2+1 + addheight,color.getRGB());
					writtenimage.setRGB(n1*2+1 + addwidth,n2*2+1 + addheight,color.getRGB());
				}
				if(five == 2){
					writtenimage.setRGB(n1*2 + addwidth,n2*2 + addheight,color2.getRGB());
					writtenimage.setRGB(n1*2+1 + addwidth,n2*2 + addheight,color.getRGB());
					writtenimage.setRGB(n1*2 + addwidth,n2*2+1 + addheight,color.getRGB());
					writtenimage.setRGB(n1*2+1 + addwidth,n2*2+1 + addheight,color2.getRGB());
				}
				if(five == 1){
					writtenimage.setRGB(n1*2 + addwidth,n2*2 + addheight,color.getRGB());
					writtenimage.setRGB(n1*2+1 + addwidth,n2*2 + addheight,color2.getRGB());
					writtenimage.setRGB(n1*2 + addwidth,n2*2+1 + addheight,color2.getRGB());
					writtenimage.setRGB(n1*2+1 + addwidth,n2*2+1 + addheight,color2.getRGB());
				}
				if(five == 0){
					writtenimage.setRGB(n1*2 + addwidth,n2*2 + addheight,color2.getRGB());
					writtenimage.setRGB(n1*2+1 + addwidth,n2*2 + addheight,color2.getRGB());
					writtenimage.setRGB(n1*2 + addwidth,n2*2+1 + addheight,color2.getRGB());
					writtenimage.setRGB(n1*2+1 + addwidth,n2*2+1 + addheight,color2.getRGB());
				}

			}
		}

		Thread work = new arbeiter(writtenimage);
	} 
}