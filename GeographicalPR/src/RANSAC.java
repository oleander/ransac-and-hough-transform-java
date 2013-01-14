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
    private ArrayList<Point> data = new ArrayList<Point>();;
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
        
        RANSAC ransac = new RANSAC(args[0]);
        RANSACResult r = ransac.execute();
        System.out.println(r.getCircle());
        ransac.showCanvas(r);
    }
    
    public void showCanvas(final RANSACResult r){
        final Circle c = r.getCircle();
        final ArrayList<Point> cs = r.getConsensusSet();
        JFrame frame = new JFrame();
        final int width = this.width;
        final int height = this.height;
        final int pointSize = this.pointSize;
        final double offsetWidth = width / 2.0 - 100.0;
        final double offsetHeight = height / 2.0 - 300.0;

        frame.add(new Canvas(){
            @Override
            public void paint(Graphics g){
                g.setColor(Color.BLACK);
                for(Point point : data){
                    g.drawOval(
                        (int) (point.getX() + offsetWidth - pointSize / 2.0 + 0.5),
                        (int) (point.getY() + offsetHeight - pointSize / 2.0 + 0.5), 
                        pointSize, 
                        pointSize
                    );
                }
                
                g.setColor(Color.RED);
                g.drawOval(
                    (int) (c.getX() - c.getRadius() + offsetWidth + 0.5),
                    (int) (c.getY() - c.getRadius() + offsetHeight + 0.5),
                    (int) (2 * c.getRadius()), 
                    (int) (2 * c.getRadius())
                );

                g.setColor(Color.GREEN);
                for(Point point : cs) {
                    g.fillOval(
                        (int) (point.getX() + offsetWidth - pointSize / 2.0 + 0.5),
                        (int) (point.getY() + offsetHeight - pointSize / 2.0 + 0.5), 
                        pointSize, 
                        pointSize
                    );
                }
            }
        });
        frame.setSize(width, height);
        frame.setVisible(true);
    }
  
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
            consensusSet = new ArrayList<Point>();

            // maybe_inliers := n randomly selected values from data
            maybeInliers = this.getNPoints();

            // maybe_model := model parameters fitted to maybe_inliers
            maybeCircle = this.getCircle(maybeInliers);

            for (int j = 0; j < this.data.size(); j++) {
                if(maybeInliers.contains(j)){ continue; }

                Point point = this.data.get(j);

                double a = point.getX();
                double b = point.getY();
                double x = maybeCircle.getX();
                double y = maybeCircle.getY();

                double hyp = Math.sqrt(Math.pow(a - x, 2) + Math.pow(b - y, 2));
                double offset = Math.abs(maybeCircle.getRadius() - hyp);

                if(offset < this.threshold){
                    // System.out.println(offset);
                    // consensus_set := maybe_inliers
                    consensusSet.add(point);
                    // System.out.println(point.getX() + "," + point.getY());
                    
                    if(consensusSet.size() > this.sufficientSize) { break; }
                } else {
                    point.addOffset(offset);
                    // System.out.println("A:" + a + ", B: " + b + ", H: " + hyp + ", O: " + offset + ", X " + x + ", Y " + y);
                }
            }

            if(consensusSet.size() > bestConsensusSet.size()){
                bestConsensusSet = consensusSet;
                bestCircle = maybeCircle;
            }

            if(consensusSet.size() > 3) {
                // System.out.println("SIZE: " + consensusSet.size() + ", R: " + maybeCircle.getRadius());
            }

            if(consensusSet.size() > this.sufficientSize) { break; }
        }
        
        for (Point point : this.data) {
            if(bestConsensusSet.contains(point)) { continue; }
            System.out.println(point.getX() + "," + point.getY() + "OFFSET: " + point.getOffset());

        }
        System.out.println("=====> BEST! --- SIZE: " + bestConsensusSet.size() + ", R: " + bestCircle.getRadius());
        
        return new RANSACResult(bestConsensusSet, bestCircle);
    }

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
  
    private class RANSACResult {
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