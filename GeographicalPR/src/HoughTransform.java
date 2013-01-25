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
    private final int minRadius         = 30;
    private final int maxRadius         = 130;
    
    /* Max and minimal center x coordinates for the given circles */
    private final int minXCoord          = -200;
    private final int maxXCoord          = 200;
      
    /* Max and minimal center y coordinates for the given circles */
    private final int minYCoord          = -200;
    private final int maxYCoord          = 200;
    
    /* The size of a cell according to the Hough Transform algorithm */
    private final int radiusSize        = 6;
    private final int cellSize          = 6;

    /* How close could two circles lie be without being the same circle? */
    private final int minCircleDistance = 20;
    private final int minRadiusDiff     = 20;

    /* View related */
    private final int pointSize         = 4;
    private final int height            = 800;
    private final int width             = 800;

    private AccumulatorWrapper pixels   = null;
    private ArrayList<Point> data       = new ArrayList<Point>();
    private ArrayList<Circle> circles   = null;
    private ArrayList<Point> consensusSet = null;

    /**
        Runs the Hough Transform algorithm and renders the result on a canvas
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

        this.pixels = new AccumulatorWrapper(
            this.minXCoord, 
            this.maxXCoord, 
            this.minYCoord, 
            this.maxYCoord, 
            this.minRadius,
            this.maxRadius,
            this.cellSize,
            this.radiusSize
        );
    }

    /**
        Executes the Hough Transform algorithm
        Populates this.pixels for later use by this.showCanvas()
    */
    public void execute(){
        // For every point given by the user
        for (Point point : this.data) {
            // Find every circle which @point.getX() and @point.getY() lies on
            ArrayList<Circle> circles = this.getCircles(point);

            // For every found circle
            for (Circle circle : circles) {
                // Give the cell which contains @circle.getX(), @circle.getY() and @circle.getRadius() a +1
                this.pixels.increment(circle.getX(), circle.getY(), circle.getRadius());
            }
        }

        this.circles = this.filterNeighbors(this.pixels.getCandidates());

        this.consensusSet = new ArrayList<Point>();
        for (Circle circle : this.circles) {
            for (Point point : this.data) {
                double x1 = circle.getX() + this.cellSize / 2.0;
                double y1 = circle.getY() + this.cellSize / 2.0;
                double offset = this.getOffset(point, x1, y1);

                if(Math.abs(circle.getRadius() - offset) <= (this.radiusSize / 2.0)){
                    this.consensusSet.add(point);
                }

                double x2 = circle.getX() - this.cellSize / 2.0;
                double y2 = circle.getY() + this.cellSize / 2.0;
                offset = this.getOffset(point, x2, y2);

                if(Math.abs(circle.getRadius() - offset) <= (this.radiusSize / 2.0)){
                    this.consensusSet.add(point);
                }

                double x3 = circle.getX() + this.cellSize / 2.0;
                double y3 = circle.getY() - this.cellSize / 2.0;
                offset = this.getOffset(point, x3, y3);

                if(Math.abs(circle.getRadius() - offset) <= (this.radiusSize / 2.0)){
                    this.consensusSet.add(point);
                }


                double x4 = circle.getX() - this.cellSize / 2.0;
                double y4 = circle.getY() - this.cellSize / 2.0;
                offset = this.getOffset(point, x4, y4);

                if(Math.abs(circle.getRadius() - offset) <= (this.radiusSize / 2.0)){
                    this.consensusSet.add(point);
                }

            }
        }
    }

    private double getOffset(Point point, double x2, double y2) {
        double x1 = point.getX();
        double y1 = point.getY();
        
        //Pythagorean theorem
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    } 
    /**
        Render view based on this.pixels
    */
    public void showCanvas() throws IllegalArgumentException {
        final ArrayList<Circle> circles = this.circles;
        final int width                 = this.width;
        final int height                = this.height;
        final int pointSize             = this.pointSize;
        final double offsetWidth        = this.width / 2.0;
        final double offsetHeight       = this.height / 2.0;
        final ArrayList<Point> data     = this.data;
        final ArrayList<Point> consensusSet     = this.consensusSet;
        JFrame frame                    = new JFrame();

        if(circles.isEmpty()){
            throw new IllegalArgumentException("No circles were found, have you tried running the execute method?");
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
                
                g.setColor(Color.RED);
                for (Circle c : circles) {
                    g.drawOval(
                        (int) (c.getX() - c.getRadius() + 0.5),
                        (int) (c.getY() - c.getRadius() + 0.5),
                        (int) (2 * c.getRadius()), 
                        (int) (2 * c.getRadius())
                    );
                }

                g.setColor(Color.GREEN);
                for(Point point : consensusSet){
                    g.drawOval(
                        (int) (point.getX() + pointSize / 2.0 + 0.5),
                        (int) (point.getY() + pointSize / 2.0 + 0.5), 
                        pointSize, 
                        pointSize
                    );
                }
            }
        });
        frame.setSize(this.width, this.height);
        frame.setVisible(true);
    }

    /*
        Find every circle that has @point.getY() and @point.getX() as its center
    */
    private ArrayList<Circle> getCircles(Point point){
        ArrayList<Circle> circles = new ArrayList<Circle>();
        double x                  = point.getX();
        double y                  = point.getY();

        for (int r = this.minRadius; r < this.maxRadius; r++) {
            for (int b = this.minYCoord; b < this.maxYCoord; b++) {
                double res = -1 * b * b + 2 * b * y + r * r - y * y;
                if(res < 0) continue;

                double a1 = x - Math.sqrt(res);
                double a2 = x + Math.sqrt(res);

                if(this.minXCoord > a1) continue;
                if(this.maxXCoord < a1) continue;

                int a11 = (int) Math.round(a1);
                int a22 = (int) Math.round(a2);
                if(a11 < this.minXCoord || a11 > this.maxXCoord) continue;
                if(a22 < this.minXCoord || a22 > this.maxXCoord) continue;

                circles.add(new Circle(a11, b, r));
                circles.add(new Circle(a22, b, r));
            }
        }
        return circles;
    }

    /* 
         Removes circles that are too similar to nearby circles
		 Max distance between two circles is defined by {this.minCircleDistance}
		 Max difference between two radii defined by {this.minRadiusDiff}
    */
    private ArrayList<Circle> filterNeighbors(ArrayList<CircleContainer> circles){
        Circle currCircle                                = null;
        ArrayList<CircleContainer> foundCircleContainers = new ArrayList<CircleContainer>();
        ArrayList<Circle> foundCircles                   = new ArrayList<Circle>();
        double distance                                  = -1;
        boolean run                                      = true;
        int bestIndex                                    = 0;

        for(CircleContainer container : circles){
            currCircle = container.getCircle();
            for (int i = 0; i < foundCircleContainers.size(); i++) {
                CircleContainer foundCircleContainer = foundCircleContainers.get(i);
                Circle prevCircle = foundCircleContainer.getCircle();

                // The distance between two circles
                distance = Math.sqrt(
                    Math.pow(currCircle.getX() - prevCircle.getX(), 2)
                    +
                    Math.pow(currCircle.getY() - prevCircle.getY(), 2)
                );

                // Diffrence in length between two circles
                int rDiff = Math.abs(prevCircle.getRadius() - currCircle.getRadius());

                // Are the two circles similar?
                if(distance < this.minCircleDistance && rDiff < this.minRadiusDiff) {
                    // Should we swap the current circle with the one we found now?
                    if(container.getCount() > foundCircleContainer.getCount()) {
                        bestIndex = i;
                        run = true;
                    }

                    // Do not add this circle to the list of good circles
                    run = false;
                }
            }

            if(run){
                foundCircleContainers.add(bestIndex, container);
            }

            run = true;
        }
        
        for (CircleContainer p : foundCircleContainers) {
            foundCircles.add(p.getCircle());
        }
        return foundCircles;
    }
}