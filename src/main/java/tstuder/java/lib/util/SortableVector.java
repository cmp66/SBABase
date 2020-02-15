package tstuder.java.lib.util;

/**
 * An interface allowing classes to implement a standard way (i.e. compare()
 * method) for sorting.
 *
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; v1.0,   1999-06-01,  First version.
 * &nbsp; v1.0,   1999-09-30,  Changed sort(int, int) from public to private.
 * </pre>
 *
 * @author Thomas Studer (ts)
 */
@SuppressWarnings("unchecked")
public class SortableVector extends java.util.Vector {

    private static final long serialVersionUID = 1L;
    private int[]        indexes;
   transient private Sortable     compare;
   private SwapListener swapListener;
/**
 * Construct a SortableVector.
 */
public SortableVector() {
   super();
}
/**
 * Construct a SortableVector.
 *
 * @param initialCapacity The Vector's initial capacity.
 * @see java.util.Vector
 */
public SortableVector(int initialCapacity) {
   super(initialCapacity);
}
/**
 * Construct a SortableVector.
 *
 * @param initialCapacity The Vector's initial capacity.
 * @param capacityIncrement The number of elements the Vector will grow 
 * if an increase of the Vector's capacity becomes necessary.
 * @see java.util.Vector
 */
public SortableVector(int initialCapacity, int capacityIncrement) {
   super(initialCapacity, capacityIncrement);
}
/**
 * 
 * 
 * @param newSwapListener tstuder.java.lib.util.SwapListener
 */
public void addSwapListener(SwapListener newSwapListener) {
	
	swapListener = newSwapListener;
}
/**
 * 
 * 
 * @return tstuder.java.lib.util.SwapListener
 */
public SwapListener getSwapListener() {
	
	return swapListener;
}
/**
 * Linearly search through the Vector looking for a particular element.
 * (The element "el" for which
 * item.compare( item, el ) evaluates to 0.)
 *
 * @param comp The object to look for (based on the compare method its
 * class implements.
 * @return The first matching element in the Vector or <code>null</code>.
 */
public synchronized Object linearLookup(Sortable item)  {

	for (int i=0; i<size(); i++) {
		if (item.compare( item, elementAt( i ) ) == 0) {
			return elementAt( i );
		}
	}

	return null;
}
/**
 * Linearly search through the Vector looking for a particular element.
 * (The element "el" for which
 * item.compare( item, el ) evaluates to 0.)
 *
 * @param comp The object to look for (based on the compare method its
 * class implements.
 * @param startIndex The index from which to start the search.
 * @return The index of the first matching element in the Vector 
 * or -1 if not found.
 */
public synchronized int linearLookup(Sortable item, int startIndex)  {

	for (int i=startIndex; i<size(); i++) {
		if (item.compare( item, elementAt( i ) ) == 0) {
			return i;
		}
	}

	return -1;
}
/**
 * Quicksort a range of Vector elements.
 *
 * @param start The index of the first element of the range.
 * @param end The index of the last element of the range.
 */
private void sort(int start, int end) {
   // do nothing if array contains fewer than two elements
   if (start >= end) 
	   return;
   swap(start, (start+end) / 2);
   int last = start;
   if (indexes == null) {
	  for (int i=start+1; i <= end; i++) {
		 if (compare.compare(elementData[start], elementData[i])  > 0)
			swap(++last, i);
	  }
   }
   else {
	  for (int i=start+1; i <= end; i++) {
		 if (compare.compare(elementData[indexes[start]], 
				elementData[indexes[i]]) > 0) {
			swap(++last, i);
		 }
	  }
   }
   swap(start, last);
   sort(start, last-1);
   sort(last+1, end);
}
/**
 * Sort a range of the Vector.
 *
 * @param start The first index of the range.
 * @param end The last index of the range.
 * @param comp Sortable The class implementing the Sortable interface
 * which provides the compare() method.
 */
public synchronized void sort(int start, int end, Sortable comp) {
   sort(start, end, comp, null);
}
/**
 * Sort a specified range of the Vector. If 'indexes' is not null,
 * the original Vector is left alone and indexes receives the indexes
 * of the elements in the range in sorted order.
 * 
 * @param start The first index of the range.
 * @param end The last index of the range.
 * @param comp The class implementing the Sortable interface providing
 * the compare() method.
 * @param indexes Can be null. If not, it receives the indexes of the
 * sorted range of Vector elements.
 */
public synchronized void sort(int start, int end, Sortable comp, int[] indexes ) {
   this.compare = comp;
   this.indexes = indexes;
   if (indexes != null) {
	  for (int i=start; i <= end; i++)
		 indexes[i] = i;
   }
   sort(start, end);
}
/**
 * Sort the Vector using the Sortable interface passed in 'comp'.
 *
 * @param comp The class implementing the Sortable interface providing
 * the compare() method.
 */
public synchronized void sort(Sortable comp)  {
   sort(0, size()-1, comp);
}
/**
 * Swap to Vector elements.
 * @param i The index of the first element to swap.
 * @param j The index of the second element to swap.
 */
protected final void swap(int i, int j) {
   if (indexes == null) {
	  Object tmp = elementData[i];
	  elementData[i] = elementData[j];
	  elementData[j] = tmp;
   } else {
	  int tmp = indexes[i];
	  indexes[i] = indexes[j];
	  indexes[j] = tmp;
   }

   if (swapListener != null) swapListener.swapped( i, j );
}
}
