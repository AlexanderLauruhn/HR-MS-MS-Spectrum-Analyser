import javax.swing.*;
import java.awt.*;

public class YAxis extends JPanel {
	/**
	 * this JPanel shows the y-axis, which has a static position in contrast to zoomable x-axis
	 * @param g the <code>Graphics</code> object to protect
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        g.setColor(Color.WHITE);
        g.fillRect(0,0,70,350); //white background
        g.setColor(Color.BLACK);
        g.drawLine(50,280,50,60);//several interceptions
 		g.drawLine(45,80, 50, 80);
 		g.drawLine(45,170, 50, 170);
 		g.drawLine(40,270,70,270);
 		g.drawString("100",20,85);
 		g.drawString("50",23,175);
		Graphics2D gx = (Graphics2D) g;
		gx.rotate(-1.57, 35, 30);//rotate 90 degrees
		gx.drawString("Intensity [%]", -140, 10);
         
	}
}