package org.ocelot.tunes4j.utils;

class MissingSequentialGenerator {

	
	private static MissingSequentialGenerator generator = new MissingSequentialGenerator();

	private MissingSequentialGenerator() {
	}

	public static MissingSequentialGenerator getInstance() {
		return generator;
	}
	
	
	public static int findFirst(int array[], int start, int end) {
		
		if (start > end ) {
			throw new IllegalArgumentException(String.format("Start:%s cannot be greater than End:%s", start, end));
		}
		
		if (array==null || array.length == 0) {
			return start;
		}
		
		int startIndex = 0;
		if ((end-start >0) && (array.length > end - start)) {
			startIndex = skipIndex(array, start);
		}
		
		for(int i=start, j=startIndex; i < end + 1 ; i++, j++) {
			if (array.length == j) return i;
			if (array[j] != i)  return i;
		}
		return -1;
	}
	
	private static int skipIndex(int[] array, int start) {
		for(int i=0; i < array.length; i++) {
			if(array[i] >= start) {
				return i;
			}
		}
		return 0;
	}
		
}
