import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;

import RANSAC.Circle;
import RANSAC.RANSACResult;


public class FrameWithCanvas extends JFrame {
	
	public FrameWithCanvas(){
		final RANSACResult r = this.execute();
        final Circle circle = r.getCircle();
        final ArrayList<Point> consensusSet = r.getConsensusSet();
        JFrame frame = new JFrame();
        final int width = this.width;
        final int height = this.height;
        final int pointSize = this.pointSize;
        final double offsetWidth = width / 2.0;
        final double offsetHeight = height / 2.0;
        final ArrayList<Point> data = this.data;

        frame.add(new Canvas(){
            @Override
            public void paint(Graphics g){
                double offsetWidth = this.getWidth() / 2.0;
                double offsetHeight = this.getHeight() / 2.0;
                g.translate((int) Math.round(offsetWidth), (int) Math.round(offsetHeight));
                for(Point point : data){
                    g.drawOval(
                        (int) (point.getX() + pointSize / 2.0 + 0.5),
                        (int) (point.getY() + pointSize / 2.0 + 0.5), 
                        pointSize, 
                        pointSize
                    );
                }
                
                // g.setColor(Color.RED);
                // g.drawOval(
                //     (int) (circle.getX() - circle.getRadius() + 0.5),
                //     (int) (circle.getY() - circle.getRadius() + 0.5),
                //     (int) (2 * circle.getRadius()), 
                //     (int) (2 * circle.getRadius())
                // );

                // g.setColor(Color.GREEN);
                // for(Point point : consensusSet) {
                //     g.fillOval(
                //         (int) (point.getX() + pointSize / 2.0 + 0.5),
                //         (int) (point.getY() + pointSize / 2.0 + 0.5), 
                //         pointSize, 
                //         pointSize
                //     );
                // }


                // double highestRadius = -1;
                // double smallestRadius = Double.POSITIVE_INFINITY;
                // for(Point point : r.getConsensusSet()){
                //     double distance = Math.sqrt(
                //         Math.pow(point.getX() - circle.getX(), 2) + 
                //         Math.pow(point.getY() - circle.getY(), 2)
                //     );

                //     if(distance > highestRadius) {
                //         highestRadius = distance;
                //     }

                //     if(distance < smallestRadius) {
                //         smallestRadius = distance;
                //     }
                // }

                // g.setColor(Color.BLUE);
                // g.drawOval(
                //     (int) (circle.getX() - highestRadius + 0.5),
                //     (int) (circle.getY() - highestRadius + 0.5),
                //     (int) (2 * highestRadius), 
                //     (int) (2 * highestRadius)
                // );

                // g.setColor(Color.ORANGE);
                // g.drawOval(
                //     (int) (circle.getX() - smallestRadius + 0.5),
                //     (int) (circle.getY() - smallestRadius + 0.5),
                //     (int) (2 * smallestRadius), 
                //     (int) (2 * smallestRadius)
                // );

                // System.out.println("smallestRadius=" + smallestRadius);
                // System.out.println("highestRadius=" + highestRadius);
            }

        });
        this.setSize(width, height);
        this.setVisible(true);
		
	}
	

}
