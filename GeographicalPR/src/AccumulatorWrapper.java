import java.util.*;
/**
 * A wrapper class for the 3D matrix that stores the circles 
 */
public class AccumulatorWrapper {
    private double cellSize;
    private double radiusSize;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int maxR;
    private int minR;
    private int r;
    private int[][][] store;
    private final int queueLimit = 10;

    public AccumulatorWrapper(
        int minX, 
        int maxX, 
        int minY, 
        int maxY, 
        int minR, 
        int maxR, 
        int cellSize,
        int radiusSize
    ){
        this.minR = minR;
        this.maxR = maxR;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.cellSize = cellSize;
        this.radiusSize = radiusSize;

        int x = (int) (this.getXSpan() / this.cellSize + 0.5);
        int y = (int) (this.getYSpan() / this.cellSize + 0.5);
        int r = (int) (this.getRSpan() / this.radiusSize + 0.5);
        
        System.out.println(x);
        System.out.println(y);
        System.out.println(r);

        this.store = new int[x][y][r];
    }

    public int get(int x, int y, int r) {
        return  this.store[this.getXCell(x)][this.getYCell(y)][this.getRCell(r)];
    }

    public void set(int x, int y, int r, int value) {
        this.store[this.getXCell(x)][this.getYCell(y)][this.getRCell(r)] = value;
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
    
    /**
     * Finds the circles with the highest score 
     * @param the number of models to return
     * @return a list containing the top n models
     */
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
            }

            if(!skip){
                circles.add(currCircle);
            }

            skip = false;
        }

        return circles;
    }

    private int getYCell(int y){
        return (int) ((y - this.minY - 1) / this.cellSize);
    }

    private int getXCell(int x){
        return (int) ((x - this.minX - 1) / this.cellSize);
    }

    private int getRCell(int r){
        return (int) ((r - this.minR - 1) / this.radiusSize);
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
}