import java.awt.*; 
import java.awt.event.*; 
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;

class AppFrame extends JFrame { 
    public AppFrame(String title) {
	super(title);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } 
}

class AppDrawPanel extends JPanel {  
    public Dimension getPreferredSize() {
        return new Dimension(500, 200);
    }
}

class AppMouseAdapter extends MouseAdapter {
    public void mouseClicked(MouseEvent e) { 
	if (e.getClickCount() > 1) 
	    System.exit(0); 
    }
}

public class AppDrawEvent
{ 
    public static void main( String[] args ) 
    {
		int imgw;
		int imgh;	
		JFrame frame = new AppFrame("Output");
		JPanel panel = new JPanel();
		JPanel imagepanel = new JPanel();
		imagepanel.setLayout(new FlowLayout());

		JPanel buttonmenu = new JPanel(new FlowLayout());
		JButton b1 = new JButton("Orginal");
		JButton b2 = new JButton("Grayscale");
		JButton b3 = new JButton("Pattern");

		
		BufferedImage orginalJPanel();JPanel(); = ImageIO.read("bird.png");
		JLabel image = new JLabel(new ImageIcon(orginal));

		imgw = orginal.getWidth(null);
		imgh = orginal.getHeight(null);

		for(int n1 = 0; n1 < imgw; n1++){
			for(int n2 = 0; n2 < imgh; n2++){
				System.out.println(orginal.getRGB(n1,n2));
			}
		}

		imagepanel.add(image);
		buttonmenu.add(b1);
		buttonmenu.add(b2);
		buttonmenu.add(b3);

		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.add(buttonmenu);
		panel.add(imagepanel);
	

		frame.add(panel);
		frame.pack();
		frame.setVisible(true); 
    } 
}