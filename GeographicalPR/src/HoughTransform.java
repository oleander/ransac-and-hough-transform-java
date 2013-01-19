import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.*;
import java.awt.Point;
import javax.swing.JFrame;
import java.io.*;
import java.awt.Point;
import java.lang.IllegalArgumentException;

public class HoughTransform {
    /* Max and minimal radius of the circles we want to find */
    private final int minRadius       = 30;
    private final int maxRadius       = 130;
    
    /* Max and minimal center x coordinates for the given circles */
    private final int minXCord        = -200;
    private final int maxXCord        = 200;
      
    /* Max and minimal center y coordinates for the given circles */
    private final int minYCord        = -200;
    private final int maxYCord        = 200;

    /* Min amount of distance between each circle */
    private final int threshold       = 15;

    /* Number of circles to be found */
    private final int numberOfCircles = 4;

    /* View related */
    private final int pointSize       = 4;
    private final int height          = 800;
    private final int width           = 800;

    private SLC pixels                = null;
    private ArrayList<Point> data     = new ArrayList<Point>();

    /**
        @args[0] File containing data points
    */
    public static void main(String[] args) throws IllegalArgumentException { 
        if(args.length == 0){
            throw new IllegalArgumentException();
        }
        
        HoughTransform h = new HoughTransform(args[0]);
        h.execute();
        h.showCanvas();
    }

    /**
        @filePath Path to file containing points
    */
    public HoughTransform(String filePath) {
        Scanner sc = null;

        try {
            sc = new Scanner(new File(filePath));
        } catch(FileNotFoundException e) { }

        if(sc == null) {
            System.out.println("Something went wrong!");
        } else {
            while(sc.hasNext()){
                String[] points = sc.next().split(",");
                int x = Integer.parseInt(points[0]);
                int y = Integer.parseInt(points[1]);
                this.data.add(new Point(x, y));
            }
        }

        this.pixels = new SLC(
            this.minXCord, 
            this.maxXCord, 
            this.minYCord, 
            this.maxYCord, 
            this.minRadius,
            this.maxRadius,
            this.threshold
        );
    }

    /**
        Executes the Hough Transform algorithm
        Populates this.pixels for later use by this.showCanvas()
    */
    public void execute(){
        for (Point point : this.data) {
            ArrayList<Circle> circles = this.getCircles(point);
            for (Circle circle : circles) {
                this.pixels.increment(circle.getX(), circle.getY(), circle.getRadius());
            }
        }
    }

    /**
        Render view based on this.pixels
    */
    public void showCanvas() throws IllegalArgumentException {
        final ArrayList<Circle> circles = this.pixels.getTopN(this.numberOfCircles);
        final int width                 = this.width;
        final int height                = this.height;
        final int pointSize             = this.pointSize;
        final double offsetWidth        = this.width / 2.0;
        final double offsetHeight       = this.height / 2.0;
        final ArrayList<Point> data     = this.data;
        JFrame frame                    = new JFrame();

        if(circles.isEmpty()){
            throw new IllegalArgumentException("No circles where found, have you tried running the execute method?");
        }

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
                
                for (Circle c : circles) {
                    g.setColor(Color.RED);
                    g.drawOval(
                        (int) (c.getX() - c.getRadius() + 0.5),
                        (int) (c.getY() - c.getRadius() + 0.5),
                        (int) (2 * c.getRadius()), 
                        (int) (2 * c.getRadius())
                    );
                }
            }
        });
        frame.setSize(this.width, this.height);
        frame.setVisible(true);
    }

    private ArrayList<Circle> getCircles(Point point){
        ArrayList<Circle> circles = new ArrayList<Circle>();
        double x = point.getX();
        double y = point.getY();


        for (int r = this.minRadius; r < this.maxRadius; r++) {
            for (int b = this.minYCord; b < this.maxYCord; b++) {
                double res = -1 * b * b + 2 * b * y + r * r - y * y;
                if(res < 0) continue;

                double a1 = x - Math.sqrt(res);
                double a2 = x + Math.sqrt(res);

                if(this.minXCord > a1) continue;
                if(this.maxXCord < a1) continue;

                int a11 = (int) Math.round(a1);
                int a22 = (int) Math.round(a2);
                if(a11 < this.minXCord || a11 > this.maxXCord) continue;
                if(a22 < this.minXCord || a22 > this.maxXCord) continue;

                circles.add(new Circle(a11, (int) b, r));
                circles.add(new Circle(a22, (int) b, r));
            }
        }
        return circles;
    }
}