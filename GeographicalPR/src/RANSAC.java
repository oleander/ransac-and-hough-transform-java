import java.awt.Point;
import java.util.Set;
import java.util.*;

public class RANSAC {
    private ArrayList<Point> data;
    private int[] model;
    private int sampleSize = 3;
    private int maxIter;
    private int threshold;
    
    public static void main(String[] args) { }
    
    /*
    input:
    data - a set of observations
    model - a model that can be fitted to data 
    n - the minimum number of data required to fit the model
    k - the number of iterations performed by the algorithm
    t - a threshold value for determining when a datum fits a model
    d - the number of close data values required to assert that a model fits well to data
output:
    best_model - model parameters which best fit the data (or nil if no good model is found)
    best_consensus_set - data points from which this model has been estimated
    best_error - the error of this model relative to the data 

iterations := 0
best_model := nil
best_consensus_set := nil
best_error := infinity
while iterations < k 
    maybe_inliers := n randomly selected values from data
    maybe_model := model parameters fitted to maybe_inliers
    consensus_set := maybe_inliers

    for every point in data not in maybe_inliers 
        if point fits maybe_model with an error smaller than t
            add point to consensus_set
    
    if the number of elements in consensus_set is > d 
        (this implies that we may have found a good model,
        now test how good it is)
        this_model := model parameters fitted to all points in consensus_set
        this_error := a measure of how well this_model fits these points
        if this_error < best_error
            (we have found a model which is better than any of the previous ones,
            keep it until a better one is found)
            best_model := this_model
            best_consensus_set := consensus_set
            best_error := this_error
     
    increment iterations

return best_model, best_consensus_set, best_error
*/
  
    public RANSACResult execute(){
        ArrayList<Point> maybeInliers = null;
        Circle maybeCircle = null;

        // maybe_inliers := n randomly selected values from data
        // maybe_model := model parameters fitted to maybe_inliers
        // consensus_set := maybe_inliers

        // while iterations < k
        for (int i = 0; i < this.maxIter; i++) {
            // maybe_inliers := n randomly selected values from data
            maybeInliers = this.getNPoints();

            // maybe_model := model parameters fitted to maybe_inliers
            maybeCircle = this.getCircle(maybeInliers);
        }

        return null;
    }

    private Circle getCircle(ArrayList<Point> workingPoints){
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
    
    private ArrayList<Point> getNPoints(){
        ArrayList<Integer> collectedNumbers = null;
        ArrayList<Point> result             = null;
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

        for (int i = 0; i < collectedNumbers.size(); i++) {
            result.add(data.get(collectedNumbers.get(i)));
        }

        return result;
    }
  
  private class RANSACResult {
      private int[] bestModel;
      private Set<Point> bestConsenusSet;
      private int best_score;
      
      public int[] getBestModel() {
        return bestModel;
      }
      public Set<Point> getBestConsenusSet() {
        return bestConsenusSet;
      }
      public int getBest_score() {
        return best_score;
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
  }
}