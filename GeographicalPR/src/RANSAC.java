import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.MyPoint;
import java.util.*;
import java.io.*;
import java.awt.MyPoint;
import java.lang.IllegalArgumentException;

import javax.swing.JFrame;

public class RANSAC {
    private ArrayList<MyPoint> data = new ArrayList<MyPoint>();;
    private final int maxIter        = 100000;
    private final int threshold      = 10;
    private final int sufficientSize = (int) Double.POSITIVE_INFINITY;
    private final int width = 1000;
    private final int height = 1000;
    private final int MyPointSize = 4;

    /**
        @filePath Path to file containing MyPoints      
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
                String[] MyPoints = sc.next().split(",");
                int x = Integer.parseInt(MyPoints[0]);
                int y = Integer.parseInt(MyPoints[1]);
                this.data.add(new MyPoint(x, y));
            }
        }
    }

    /**
        @args[0] Path to file containing MyPoints      
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
        final ArrayList<MyPoint> cs = r.getConsensusSet();
        JFrame frame = new JFrame();
        final int width = this.width;
        final int height = this.height;
        final int MyPointSize = this.MyPointSize;
        final double offsetWidth = width / 2.0 - 100.0;
        final double offsetHeight = height / 2.0 - 300.0;

        frame.add(new Canvas(){
            @Override
            public void paint(Graphics g){
                g.setColor(Color.BLACK);
                for(MyPoint MyPoint : data){
                    g.drawOval(
                        (int) (MyPoint.getX() + offsetWidth - MyPointSize / 2.0 + 0.5),
                        (int) (MyPoint.getY() + offsetHeight - MyPointSize / 2.0 + 0.5), 
                        MyPointSize, 
                        MyPointSize
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
                for(MyPoint MyPoint : cs) {
                    g.fillOval(
                        (int) (MyPoint.getX() + offsetWidth - MyPointSize / 2.0 + 0.5),
                        (int) (MyPoint.getY() + offsetHeight - MyPointSize / 2.0 + 0.5), 
                        MyPointSize, 
                        MyPointSize
                    );
                }
            }
        });
        frame.setSize(width, height);
        frame.setVisible(true);
    }
  
    public RANSACResult execute() throws IllegalArgumentException {
        if(this.data.size() == 0){
            throw new IllegalArgumentException("File containing MyPoints is empty");
        }

        ArrayList<Integer> maybeInliers   = new ArrayList<Integer>();
        ArrayList<MyPoint> bestConsensusSet = new ArrayList<MyPoint>();;
        ArrayList<MyPoint> consensusSet     = null;
        Circle bestCircle                 = null;
        Circle maybeCircle                = null;

        // while iterations < k
        for (int i = 0; i < this.maxIter; i++) {
            consensusSet = new ArrayList<MyPoint>();

            // maybe_inliers := n randomly selected values from data
            maybeInliers = this.getNMyPoints();

            // maybe_model := model parameters fitted to maybe_inliers
            maybeCircle = this.getCircle(maybeInliers);

            for (int j = 0; j < this.data.size(); j++) {
                if(maybeInliers.contains(j)){ continue; }

                MyPoint MyPoint = this.data.get(j);

                double a = MyPoint.getX();
                double b = MyPoint.getY();
                double x = maybeCircle.getX();
                double y = maybeCircle.getY();

                double hyp = Math.sqrt(Math.pow(a - x, 2) + Math.pow(b - y, 2));
                double offset = Math.abs(maybeCircle.getRadius() - hyp);

                if(offset < this.threshold){
                    // System.out.println(offset);
                    // consensus_set := maybe_inliers
                    consensusSet.add(MyPoint);
                    // System.out.println(MyPoint.getX() + "," + MyPoint.getY());
                    
                    if(consensusSet.size() > this.sufficientSize) { break; }
                } else {
                    MyPoint.addOffset(offset);
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
        
        for (MyPoint MyPoint : this.data) {
            if(bestConsensusSet.contains(MyPoint)) { continue; }
            System.out.println(MyPoint.getX() + "," + MyPoint.getY() + "OFFSET: " + MyPoint.getOffset());

        }
        System.out.println("=====> BEST! --- SIZE: " + bestConsensusSet.size() + ", R: " + bestCircle.getRadius());
        
        return new RANSACResult(bestConsensusSet, bestCircle);
    }

    private Circle getCircle(ArrayList<Integer> workingIndexes){
        ArrayList<MyPoint> workingMyPoints = new ArrayList<MyPoint>();

        for(int index : workingIndexes) {
            workingMyPoints.add(this.data.get(index));
        }

        double a = workingMyPoints.get(0).getX();
        double b = workingMyPoints.get(0).getY();

        double c = workingMyPoints.get(1).getX();
        double d = workingMyPoints.get(1).getY();

        double e = workingMyPoints.get(2).getX();
        double f = workingMyPoints.get(2).getY();

        double k = 0.5 * ((a * a + b * b) * (e - c) + (c * c + d * d) * (a - e) + (e * e + f * f) * (c - a)) / (b * (e - c) + d * (a - e) + f * (c - a));
        double h = 0.5 * ((a * a + b * b) * (f - d) + (c * c + d * d) * (b - f) + (e * e + f * f) * (d - b)) / (a * (f - d) + c * (b - f) + e * (d - b)); 
        double r = Math.sqrt(Math.pow(a - h, 2) + Math.pow(b - k, 2));

        return new Circle(h, k, r);
    }
    
    private ArrayList<Integer> getNMyPoints(){
        ArrayList<Integer> collectedNumbers = new ArrayList<Integer>();
        ArrayList<MyPoint> result             = new ArrayList<MyPoint>();
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
      private ArrayList<MyPoint> consensusSet;

      public RANSACResult(ArrayList<MyPoint> consensusSet, Circle circle) {
        this.consensusSet = consensusSet;
        this.circle = circle;
      }

      public Circle getCircle() {
        return this.circle;
      }

      public ArrayList<MyPoint> getConsensusSet(){
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

    public class MyPoint extends Point {
        private double offset;

        public void setOffset(double offset) {
            this.offset = offset;
        }

        public double getOffset(){
            return this.offset;
        }
    }
}