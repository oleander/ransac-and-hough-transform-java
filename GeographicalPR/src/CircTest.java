import java.awt.Canvas;
import java.awt.Graphics;

import javax.swing.JFrame;


public class CircTest extends JFrame {
	
	
	public static void main(String[] args) {
		new CircTest();
	}
	
	public CircTest(){
		this.setSize(500, 600);
		this.setVisible(true);
		this.add(new Canvas(){
			
			@Override
			public void paint(Graphics g){
				
				int count = 0;
	            double radius = 100;
	            int x = 300;
	            int y = 300;
	            for (double rad = 0; rad < 2* Math.PI; rad += (1/radius) ){
	            	double a = x - radius * Math.cos(rad) + 0.5;
	            	double b = y - radius * Math.sin(rad) + 0.5;
	            	g.drawOval((int)(a - radius), (int) (b - radius), 1, 1);
	            	count++;
	            }
	            
	            System.out.println(count);
			}
			
		});
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
}
