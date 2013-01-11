import java.awt.Point;
import java.util.Set;
import java.util.*;
import java.io.*;
import java.awt.Point;
import java.lang.IllegalArgumentException;

public class RANSAC {
    private ArrayList<Point> data = new ArrayList<Point>();;
    private int sampleSize     = 3;
    private int maxIter        = 10;
    private int threshold      = 3;
    private int sufficientSize = 3;

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

        RANSACResult r = new RANSAC(args[0]).execute();
        System.out.println(r.getCircle());

    }
  
    public RANSACResult execute(){
        ArrayList<Integer> maybeInliers   = new ArrayList<Integer>();
        ArrayList<Point> consensusSet     = new ArrayList<Point>();
        ArrayList<Point> bestConsensusSet = new ArrayList<Point>();
        Circle bestCircle                 = null;
        Circle maybeCircle                = null;

        // while iterations < k
        for (int i = 0; i < this.maxIter; i++) {
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
                    // consensus_set := maybe_inliers
                    consensusSet.add(point);
                    
                    if(consensusSet.size() > this.sufficientSize) { break; }
                }
            }

            if(consensusSet.size() > bestConsensusSet.size()){
                bestConsensusSet = consensusSet;
                bestCircle = maybeCircle;
            }

            if(consensusSet.size() > this.sufficientSize) { break; }
        }

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

        double k = (1/2) * (( a * a + b * b)*(e - c) + (c * c + d * d) * (a - e) + (e * e + f * f) * (c - a)) / (b * (e - c) + d * (a - e) + f * (c - a));
        double h = (1/2) * ((a * a + b * b) * (f - d) + (c * c + d * d) * (b - f) + (e * e + f * f) * (d - b)) / (a * (f - d) + c * (b - f) + e * (d - b)); 
        double r = Math.sqrt(Math.pow(a - h, 2) + Math.pow(b - k, 2));

        return new Circle(k, h, r);
    }
    
    private ArrayList<Integer> getNPoints(){
        ArrayList<Integer> collectedNumbers = new ArrayList<Integer>();
        ArrayList<Point> result             = new ArrayList<Point>();
        Random random                       = new Random();

        for (int n = 0; n < this.sampleSize; n++) {
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