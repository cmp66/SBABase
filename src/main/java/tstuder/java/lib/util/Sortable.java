package tstuder.java.lib.util;

/**
 * An interface allowing classes to implement a standard way (i.e. compare()
 * method) for sorting.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public interface Sortable {

	/** The code returned by compare() if 'object1' < 'object2'.
	 */
   public int LESS_THAN    = -1;    

	/** The code returned by compare() if 'object1' == 'object2'.
	 */
   public int EQUAL        = 0;     


	/** The code returned by compare() if 'object1' > 'object2'.
	 */   
	public int GREATER_THAN = 1;
/**
 * Compare two objects.
 *
 * @param object1 The first of the two objects to be compared.
 * @param object2 The second of the two objects to be compared.
 * @return One of the constants defined as part of the class declaration.
 */
public int compare(Object object1, Object object2);
}
