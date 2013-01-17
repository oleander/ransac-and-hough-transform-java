import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.*;
import java.io.*;
import java.awt.Point;
import java.lang.IllegalArgumentException;

public class HoughTransform {
    private SLC pixels = new SLC(-100, 500, -150, 150, 120);
    private ArrayList<Point> data = new ArrayList<Point>();

    public static void main(String[] args) throws IllegalArgumentException { 
        if(args.length == 0){
            throw new IllegalArgumentException();
        }
        
        new HoughTransform(args[0]).execute();
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

        this.pixels.getTopN(5);
    }

    private ArrayList<Circle> getCircles(Point point){
        ArrayList<Circle> circles = new ArrayList<Circle>();
        double x = point.getX();
        double y = point.getY();

        for (int r = 0; r < 5; r++) {
            for (int b = 0; b < 5; b++) {
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
            System.out.println("X" + x + ", Y" + y + ", R" + r);
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
            PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
            ArrayList<Circle> circles = new ArrayList<Circle>();

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
                CircleComparator container = pq.poll();
                if(container == null) break;
                System.out.println("COUNT " + container.getCount());
                circles.add(container.getCircle());
            }

            return circles;
        }
    }


    // import java.util.Comparator;

    public class CircleComparator implements Comparator<Circle>
    {
        @Override
        public int compare(Circle x,  y)
        {
            // Assume neither string is null. Real code should
            // probably be more robust
            if (x.length() < y.length())
            {
                return -1;
            }
            if (x.length() > y.length())
            {
                return 1;
            }
            return 0;
        }
    }
}