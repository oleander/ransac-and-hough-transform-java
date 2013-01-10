import java.awt.Point;
import java.util.Set;

public class RANSAC {
    
    private Set<Point> data;
    private int[] model;
    private int sampleSize = 3;
    private int maxIter;
    private int threshold;
    
    
    public static void main(String[] args) {
    
    }
    
    /*input:
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
        
//        maybe_inliers := n randomly selected values from data
//        maybe_model := model parameters fitted to maybe_inliers
//        consensus_set := maybe_inliers
        
        for (int i = 0; i < maxIter; i++) {
            
        }
 
        return null;
    }
	  
  
  
  
  class RANSACResult {
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
}