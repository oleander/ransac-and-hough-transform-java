import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AWTest extends TestCase {
  private AccumulatorWrapper aw;
  public void setUp(){
    aw = new AccumulatorWrapper(-10, 15, -10, 15, 20, 150, 5, 5);
  }

  public void testInitGet(){
    boolean exp = false;
    try {
      aw.get(0, 0, 0);
    } catch(IllegalArgumentException e){
      exp = true;
    }
    assertTrue(exp);
  }

  public void testRadius(){
    assertEquals(0, aw.get(0, 0, 30));
  }
  
  public void testRadiusInc(){
    aw.set(0, 0, 30, 1);
    assertEquals(1, aw.get(0, 0, 30));
  }
  
  public void testIncSameCell(){
    for (int i = 0; i < 10; i++){
      aw.increment(i - 10, 0, 30);
    }
    
    for (int i = 0; i < 10; i++){
      assertEquals(5, aw.get(i - 10, 0, 30));
    }
  }
  
  public void testIncrement(){
    aw.increment(0, 0, 30);
    assertEquals(1, aw.get(0, 0, 30));
  }
  
  public void testIncMultiDIm(){
    int c = 3;
    for (int x = 0; x < 5*c; x++){
      for (int y = 0; y < 5*c; y++){
        for(int r = 30; r < 35*c; r++ ){
          aw.increment(x, y, r);
        }
      }
    }

    assertEquals(125, aw.get(0, 0, 30));
    assertEquals(125, aw.get(5, 5, 35));
    assertEquals(125, aw.get(10, 10, 40));
  }
  
  public void testConvertIndexToXCoord(){
    assertEquals(-7, aw.getXCoord(0));
    assertEquals(12, aw.getXCoord(4));
  }
  
  public void testConvertIndexToYCoord(){
    assertEquals(-7, aw.getYCoord(0));
    assertEquals(12, aw.getYCoord(4));
  }
  
  public void testConvertIndexToRCoord(){
    assertEquals(22, aw.getRCoord(0));
    assertEquals(27, aw.getRCoord(1));
  } 
}