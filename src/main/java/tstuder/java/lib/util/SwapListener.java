package tstuder.java.lib.util;

/**
 * An interface useful to register a swap listener with a class that swaps elements
 * like a sorting vector class. The interface contains one single method 
 * <code>swapped(int, int)</code> which is called after each swap of two elements. 
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 2000-04-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public interface SwapListener {
/**
 * Called to notify a SwapListener that two elements have been swapped (e.g. during
 * some sort operation.
 * 
 * @param i The first element index.
 * @param j The second element index.
 */
void swapped(int i, int j);
}
