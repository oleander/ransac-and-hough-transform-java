import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AWTest extends TestCase {

	
	
	private AccumulatorWrapper aw;
	
	public void setUp(){
		aw = new AccumulatorWrapper(-10, 10, -10, 10, 20, 50, 5, 5);
	}
	

	public void testInitGet(){
		
		try {
			aw.get(0, 0, 0);
		} catch(IllegalArgumentException e){
			assertTrue(false);
		}
		
			
		
		
		
	}


}