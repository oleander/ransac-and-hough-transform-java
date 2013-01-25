import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.*;
import java.io.*;
import java.awt.Point;
import java.lang.IllegalArgumentException;

import javax.swing.JFrame;

public class RANSAC {
    private ArrayList<Point> data = new ArrayList<Point>();
    private ArrayList<Boolean> correctSet = new ArrayList<Boolean>();
    private final int maxIter        = 100000;
    private final int threshold      = 10;
    private final int sufficientSize = (int) Double.POSITIVE_INFINITY;
    private final int width = 1000;
    private final int height = 1000;
    private final int pointSize = 4;

    /**
        @filePath Path to file containing points      
    */
    public RANSAC(String filePath) {
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
    }

    /**
        @args[0] Path to file containing points      
    */
    public static void main(String[] args) throws IllegalArgumentException { 
        if(args.length == 0){
            throw new IllegalArgumentException();
        }
        
        new RANSAC(args[0]).showCanvas();
    }
    
    /**
     * Displays the data points and the calculated circles on a canvas
     */
    public void showCanvas(){
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
        frame.setSize(width, height);
        frame.setVisible(true);
    }
    
    /**
     * Runs the RANSAC algorithm
     * @return a RANSACResult, which contains the circle and its corresponding consensus set
     * @throws IllegalArgumentException
     */
    public RANSACResult execute() throws IllegalArgumentException {
        if(this.data.size() == 0){
            throw new IllegalArgumentException("File containing points is empty");
        }

        ArrayList<Integer> maybeInliers   = new ArrayList<Integer>();
        ArrayList<Point> bestConsensusSet = new ArrayList<Point>();;
        ArrayList<Point> consensusSet     = null;
        Circle bestCircle                 = null;
        Circle maybeCircle                = null;

        // while iterations < k
        for (int i = 0; i < this.maxIter; i++) {
            // maybe_inliers := n randomly selected values from data
            maybeInliers = this.getNPoints();
            consensusSet = new ArrayList<Point>();
            for (int index : maybeInliers) {
                consensusSet.add(this.data.get(index));
            }

            // maybe_model := model parameters fitted to maybe_inliers
            maybeCircle = this.getCircle(maybeInliers);

            for (int j = 0; j < this.data.size(); j++) {
                if(maybeInliers.contains(j)){ continue; }

                Point point = this.data.get(j);
                double offset = this.getOffset(point, maybeCircle);
                
                // If this point is close enough to the circle, add it to the set
                if(offset < this.threshold){
                    // consensus_set := maybe_inliers
                    consensusSet.add(point);
                    
                    if(consensusSet.size() > this.sufficientSize) { break; }
                }
            }
            //Keep track of the best model so far
            if(consensusSet.size() > bestConsensusSet.size()){
                bestConsensusSet = consensusSet;
                bestCircle = maybeCircle;
            }

            if(consensusSet.size() > this.sufficientSize) { break; }
        }
                
        return new RANSACResult(bestConsensusSet, bestCircle);
    }
    
    // Calculate the distance between a point and the center of a circle
    private double getOffset(Point point, Circle circle) {
        double x1 = point.getX();
        double y1 = point.getY();
        double x2 = circle.getX();
        double y2 = circle.getY();
        
        //Pythagorean theorem
        double hyp = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        return Math.abs(circle.getRadius() - hyp);
    }

    //Find the circle that passes through the three given points 
    // http://2000clicks.com/mathhelp/GeometryConicSectionCircleEquationGivenThreePoints.aspx
    private Circle getCircle(ArrayList<Integer> workingIndexes){
        ArrayList<Point> workingPoints = new ArrayList<Point>();

        for(int index : workingIndexes) {
            workingPoints.add(this.data.get(index));
        }

        double a = workingPoints.get(0).getX();
        double b = workingPoints.get(0).getY();

        double c = workingPoints.get(1).getX();
        double d = workingPoints.get(1).getY();

        double e = workingPoints.get(2).getX();
        double f = workingPoints.get(2).getY();
        
        double k = 0.5 * ((a * a + b * b) * (e - c) + (c * c + d * d) * (a - e) + (e * e + f * f) * (c - a)) / (b * (e - c) + d * (a - e) + f * (c - a));
        double h = 0.5 * ((a * a + b * b) * (f - d) + (c * c + d * d) * (b - f) + (e * e + f * f) * (d - b)) / (a * (f - d) + c * (b - f) + e * (d - b)); 
        double r = Math.sqrt(Math.pow(a - h, 2) + Math.pow(b - k, 2));

        return new Circle(h, k, r);
    }
    
    // Choose n data points at random
    private ArrayList<Integer> getNPoints(){
        ArrayList<Integer> collectedNumbers = new ArrayList<Integer>();
        ArrayList<Point> result             = new ArrayList<Point>();
        Random random                       = new Random();

        for (int n = 0; n < 3; n++) {
            while(true){
                int possibleNumber = random.nextInt(this.data.size());
                if(!collectedNumbers.contains(possibleNumber)){
                    collectedNumbers.add(possibleNumber);
                    break;
                }
            }
        }

        return collectedNumbers;
    }
    
    // Representation of the circle model with corresponding consensus set
    public class RANSACResult {
      private Circle circle;
      private ArrayList<Point> consensusSet;

      public RANSACResult(ArrayList<Point> consensusSet, Circle circle) {
        this.consensusSet = consensusSet;
        this.circle = circle;
      }

      public Circle getCircle() {
        return this.circle;
      }

      public ArrayList<Point> getConsensusSet(){
        return this.consensusSet;
      }
    }

    private class Circle {
        private double x;
        private double y;
        private double radius;

        public Circle(double x, double y, double radius){
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public double getX(){
            return x;
        }

        public double getY(){
            return y;
        }

        public double getRadius(){
            return radius;
        }

        @Override
        public String toString(){
            return "X : " + x + ", Y: " + y + ", Radius: " + radius;
        }
    }
}