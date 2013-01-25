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
    private final int threshold = 1100;

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
        return (int) (this.getXSpan() / this.cellSize + 0.5) + 1;
    }

    private int getAllocationForY(){
        return (int) (this.getYSpan() / this.cellSize + 0.5) + 1;
    }

    private int getAllocationForR(){
        return (int) (this.getRSpan() / this.radiusSize + 0.5) + 1;
    }

    public int get(int x, int y, int r) throws IllegalArgumentException {
        try {
            return  this.store[this.getXCell(x)][this.getYCell(y)][this.getRCell(r)];
        } catch(ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException(this.blameVar(x, y, r));
        }
    }

    public void set(int x, int y, int r, int value) {
        try {
            this.store[this.getXCell(x)][this.getYCell(y)][this.getRCell(r)] = value;
        } catch(ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException(this.blameVar(x, y, r));
        }
    }

    public void increment(int x, int y, int r){
        int count = this.get(x, y, r);
        this.set(x, y, r, count + 1);
    }
    
    /**
     * Finds the circles with the highest score 
     * @param the number of models to return
     * @return a list containing the top n models
     */
    public ArrayList<CircleContainer> getCandidates(){
        ArrayList<CircleContainer> candidates = new ArrayList<CircleContainer>();
                
        for (int x = 0; x < this.getAllocationForX() - 1; x++) {
            for (int y = 0; y < this.getAllocationForY() - 1; y++) {
                for (int radius = 0; radius < this.getAllocationForR() - 1; radius++) {
                    int count = this.store[x][y][radius];
                    if(count > this.threshold){
                        candidates.add(
                            new CircleContainer(
                                new Circle(
                                    this.getXCoord(x),
                                    this.getYCoord(y),
                                    this.getRCoord(radius)
                                ),
                                count
                            )
                        );
                    }
                }
            }
        }
        return candidates;
    }

    private int getYCell(int y){
        return (int) ((y - this.minY) / this.cellSize);
    }

    private int getXCell(int x){
        return (int) ((x - this.minX) / this.cellSize);
    }

    private int getRCell(int r){
        return (int) ((r - this.minR) / this.radiusSize);
    }
    
    public int getXCoord(int xIndex){
        return (int) ((xIndex + 0.5) * this.cellSize + minX);
    }

    public int getYCoord(int yIndex){
        return (int) ((yIndex + 0.5) * this.cellSize + minY);
    }
    
    public int getRCoord(int rIndex){
        return (int) ((rIndex + 0.5) * this.radiusSize + minR);
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

    private String blameVar(int x, int y, int r){
        String error = null;
        if(this.getXCell(x) < 0 || this.getXCell(x) > this.getAllocationForX()){
            error = "x";
        } else if (this.getXCell(y) < 0 || this.getYCell(y) > this.getAllocationForY())
            error = "y";
        else {
            error = "r";
        }

        return error + " must be inside scoop: " + x + ", " + y + ", " + r;
    }
}