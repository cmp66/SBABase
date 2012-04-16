package tstuder.java.lib.util;

/**
 * A Vector that can be sorted. The objects managed by the vector are expected
 * to represent items of one or more columns of data. The ColumnSortableVector
 * allows to sort on a particular column.
 *
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-11-28, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class ColumnSortableVector extends SortableVector {

    private static final long serialVersionUID = 1L;
int[]      	 		indexes;
   transient ColumnSortable    compare;
   boolean           ascending;
/**
 * Construct a SortableVector.
 */
public ColumnSortableVector() {
   super();
}
/**
 * Construct a SortableVector.
 *
 * @param initialCapacity The Vector's initial capacity.
 * @see java.util.Vector
 */
public ColumnSortableVector(int initialCapacity) {
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
public ColumnSortableVector(int initialCapacity, int capacityIncrement) {
   super(initialCapacity, capacityIncrement);
}
/**
 * Get the 'ascending' property.
 * 
 * @return boolean
 */
public boolean isAscending() {

	return ascending;
}
/**
 * Set the 'ascending' property.
 * 
 * @param ascending <code>true</code> to sort ascending, <code>false</code> otherwise.
 */
public void setAscending( boolean ascending ) {

	this.ascending = ascending;
}
/**
 * Quicksort a range of Vector elements.
 *
 * @param columnIndex The column to sort on.
 * @param start The index of the first element of the range.
 * @param end The index of the last element of the range.
 */
public void sort(int columnIndex, int start, int end) {

   if (start >= end) return;
   
   swap(start, (start+end) / 2);
   int last = start;
   
   if (indexes == null) {
	   if (ascending) {
		  	for (int i=start+1; i <= end; i++) {
			 	if (compare.compare(elementData[start], elementData[i], columnIndex)  > 0)
					swap(++last, i);
		  	}
	   } else {
		  	for (int i=start+1; i <= end; i++) {
			 	if (compare.compare(elementData[start], elementData[i], columnIndex)  <= 0)
					swap(++last, i);
		  	}
	   }		   
	} else {
	  	if (ascending) {
		  	for (int i=start+1; i <= end; i++) {
			 	if (compare.compare(elementData[indexes[start]], 
					elementData[indexes[i]], columnIndex) > 0) {
						
					swap(++last, i);
			 	}
		  	}
	  	} else {
		  	for (int i=start+1; i <= end; i++) {
			 	if (compare.compare(elementData[indexes[start]], 
					elementData[indexes[i]], columnIndex) <= 0) {
						
					swap(++last, i);
			 	}
		  	}
	  	}		  	
   }
   
   swap(start, last);
   sort(columnIndex, start, last-1);
   sort(columnIndex, last+1, end);
}
/**
 * Sort a range of the Vector.
 *
 * @param columnIndex The column to sort on.
 * @param start The first index of the range.
 * @param end The last index of the range.
 * @param comp The class implementing the Sortable interface
 * which provides the compare(...) method.
 */
public synchronized void sort(int columnIndex, int start, int end, ColumnSortable comp) {
   sort(columnIndex, start, end, comp, null);
}
/**
 * Sort a specified range of the Vector. If 'indexes' is not null,
 * the original Vector is left alone and indexes receives the indexes
 * of the elements in the range in sorted order.
 * 
 * @param columnIndex The column to sort on.
 * @param start The first index of the range.
 * @param end The last index of the range.
 * @param comp The class implementing the Sortable interface providing
 * the compare() method.
 * @param indexes Can be null. If not, it receives the indexes of the
 * sorted range of Vector elements.
 */
public synchronized void sort(int columnIndex, int start, int end, ColumnSortable comp, int[] indexes ) {
	
   this.compare = comp;
   this.indexes = indexes;
   
   if (indexes != null) {
	  	for (int i=start; i <= end; i++)
		 	indexes[i] = i;
   }
   
   sort(columnIndex, start, end);
}
/**
 * Sort the Vector using the Sortable interface passed in 'comp'.
 *
 * @param columnIndex The column to sort on.
 * @param comp The class implementing the Sortable interface providing
 * the compare() method.
 */
public synchronized void sort(int columnIndex, ColumnSortable comp)  {
   sort(columnIndex, 0, size()-1, comp);
}
}
