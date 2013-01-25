import java.util.*;
/**
 * A wrapper class for the 3D matrix that stores the circles 
 */
public class AccumulatorWrapper {
    private int cellSize;
    private int radiusSize;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int maxR;
    private int minR;
    private int r;
    private int[][][] store;
    private final int threshold = 3000;

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

        this.store = new int[this.getAllocationForX()]
            [this.getAllocationForY()]
            [this.getAllocationForR()];
    }

    private int getAllocationForX(){
        return (int) (this.getXSpan() / this.cellSize + 0.5);
    }

    private int getAllocationForY(){
        return (int) (this.getYSpan() / this.cellSize + 0.5);
    }

    private int getAllocationForR(){
        return (int) (this.getRSpan() / this.radiusSize + 0.5);
    }

    public int get(int x, int y, int r) {
    	System.out.println(this.getRCell(r));
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
    public ArrayList<Circle> getCandidates(){
        ArrayList<Circle> candidates = new ArrayList<Circle>();
                
        for (int x = 0; x < this.getAllocationForX(); x++) {
            for (int y = 0; y < this.getAllocationForY(); y++) {
                for (int radius = 0; radius < this.getAllocationForR(); radius++) {
                    int count = this.store[x][y][radius];
                    if(count > this.threshold){
                        candidates.add(
                            new Circle(
                                x * (this.cellSize / 2), 
                                y * (this.cellSize / 2), 
                                radius * this.radiusSize
                            )
                        );
                    }
                }
            }
        }

        return candidates;
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