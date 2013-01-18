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
    private final int minRadius = 30;
    private final int maxRadius = 130;

    private final int minXCord = -200;
    private final int maxXCord = 200;

    private final int minYCord = -200;
    private final int maxYCord = 200;

    private final int threshold = 15;

    private final int numberOfCircles = 4;

    private SLC pixels = null;
    private ArrayList<Point> data = new ArrayList<Point>();
    private final int pointSize = 4;
    private final int height = 800;
    private final int width = 800;

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

    public void execute(){
        for (Point point : this.data) {
            ArrayList<Circle> circles = this.getCircles(point);
            for (Circle circle : circles) {
                this.pixels.increment(circle.getX(), circle.getY(), circle.getRadius());
            }
        }
    }

    public void showCanvas(){
        final ArrayList<Circle> circles = this.pixels.getTopN(this.numberOfCircles);
        final int width = this.width;
        final int height = this.height;
        final int pointSize = this.pointSize;
        final double offsetWidth = this.width / 2.0;
        final double offsetHeight = this.height / 2.0;
        final ArrayList<Point> data = this.data;
        JFrame frame = new JFrame();
        frame.setSize(this.width, this.height);
        frame.setVisible(true);
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

    private class Circle {
        private int x;
        private int y;
        private int radius;

        public Circle(int x, int y, int radius){
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public int getX(){
            return x;
        }

        public int getY(){
            return y;
        }

        public int getRadius(){
            return radius;
        }

        @Override
        public String toString(){
            return "X : " + x + ", Y: " + y + ", Radius: " + radius;
        }
    }

    private class SLC {
        private int minX;
        private int maxX;
        private int minY;
        private int maxY;
        private int maxR;
        private int minR;
        private int threshold;
        private int r;
        private int[][][] store;
        private final int queueLimit = 10;


        public SLC(int minX, int maxX, int minY, int maxY, int minR, int maxR, int threshold){
            this.minR = minR;
            this.maxR = maxR;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.threshold = threshold;
            this.store = new int[this.getXSpan() + 1]
                [this.getYSpan() + 1]
                [this.getRSpan() + 1];
        }

        private int getXSpan(){
            return this.maxX - this.minX;
        }

        private int getYSpan(){
            return this.maxY - this.minY;
        }

        private int getRSpan(){
            return this.maxR - this.minR;
        }

        public int get(int x, int y, int r) {
            return this.store[x - this.minX][y - this.minY][r - this.minR];
        }

        public void set(int x, int y, int r, int value) {
            this.store[x - this.minX][y - this.minY][r - this.minR] = value;
        }

        public void increment(int x, int y, int r){
            int count = this.get(x, y, r);
            this.set(x, y, r, count + 1);
        }

        public int getProperX(int x) {
            return x + this.minX;
        }

        public int getProperR(int r) {
            return r + this.minR;
        }

        public int getProperY(int y){
            return y + this.minY;
        }

        public ArrayList<Circle> getTopN(int n){
            PriorityQueue<CircleContainer> pq = new PriorityQueue<CircleContainer>();
            ArrayList<Circle> circles         = new ArrayList<Circle>();
            CircleContainer container         = null;
            Circle circle = null;

            for (int x = this.minX; x < this.maxX; x++) {
                for (int y = this.minY; y < this.maxY; y++) {
                    for (int radius = this.minR; radius < this.maxR; radius++) {
                        int count = this.get(x, y, radius);
                        if(count > this.queueLimit){
                            circle = new Circle(x, y, radius);
                            pq.add(new CircleContainer(circle, count));
                        }
                    }
                }
            }

            Circle currCircle = null;
            double distance = -1;
            boolean skip = false;

            while(circles.size() < n){
                container = pq.poll();
                if(container == null) break;
                currCircle = container.getCircle();

                for(Circle prevCircle : circles) {
                    distance = Math.sqrt(
                        Math.pow(currCircle.getX() - prevCircle.getX(), 2)
                        +
                        Math.pow(currCircle.getY() - prevCircle.getY(), 2)
                    );

                    if(distance < this.threshold) {
                        skip = true; break;
                    }
                }

                if(!skip){
                    circles.add(currCircle);
                }

                skip = false;
            }

            return circles;
        }
    }
    
    class CircleContainer implements Comparable<CircleContainer> {
        private Circle circle;
        private int count;
       
        @Override
        public int compareTo(CircleContainer cc) {
            return Double.compare(cc.count, this.count);
        }
        
        public CircleContainer(Circle circle, int count) {
            super();
            this.circle = circle;
            this.count = count;
        }

        public Circle getCircle() {
            return circle;
        }

        public int getCount() {
            return count;
        }
    }
}