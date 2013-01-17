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
    private SLC pixels = new SLC(-300, 500, -300, 150, 50);
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
        final ArrayList<Circle> circles = h.pixels.getTopN(5);
        for (Circle circle : circles) {
            System.out.println(circle.getRadius());
            System.out.println(circle.getX());
            System.out.println(circle.getY());
            System.out.println("--- - - ---- --");
        }
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
        final ArrayList<Circle> circles = this.pixels.getTopN(5);
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

        for (int r = 20; r < 21; r++) {
            for (int b = 0; b < 1; b++) {
                double a1 = x - Math.sqrt(-1 * b * b + 2 * b * y + r * r - y * y);
                double a2 = x + Math.sqrt(-1 * b * b + 2 * b * y + r * r - y * y);

                circles.add(new Circle(((int) Math.round(a1)), b, r));
                circles.add(new Circle(((int) Math.round(a2)), b, r));
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
        private int r;
        private int[][][] store;

        public SLC(int minX, int maxX, int minY, int maxY, int r){
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.r = r;
            this.store = new int[this.getXSpan()][this.getYSpan()][r];
        }

        private int getXSpan(){
            return this.maxX - this.minX;
        }

        private int getYSpan(){
            return this.maxY - this.minY;
        }

        public int get(int x, int y, int r) {
            return this.store[x - this.minX][y - this.minY][r];
        }

        public void set(int x, int y, int r, int value) {
            this.store[x - this.minX][y - this.minY][r] = value;
        }

        public void increment(int x, int y, int r){
            int count = this.get(x, y, r);
            this.set(x, y, r, count + 1);
        }

        public ArrayList<Circle> getTopN(int n){
            PriorityQueue<CircleContainer> pq = new PriorityQueue<CircleContainer>();
            ArrayList<Circle> circles         = new ArrayList<Circle>();
            CircleContainer container         = null;

            for (int x = 0; x < this.getXSpan(); x++) {
                for (int y = 0; y < this.getYSpan(); y++) {
                    for (int radius = 0; radius < this.r; radius++) {
                        int count = this.store[x][y][radius];
                        if(count != 0){
                            pq.add(new CircleContainer(new Circle(x, y, radius), count));
                        }
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                container = pq.poll();
                if(container == null) break;
                // System.out.println(container.getCircle().getRadius());
                circles.add(container.getCircle());
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