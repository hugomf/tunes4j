package org.ocelot.tunes4j.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MissingSequentialGeneratorTest {
	
	
	 @Rule
     public ExpectedException exception= ExpectedException.none();
	
	@Test
	public void shouldReturnStartWhenEmptyArray() throws Exception {
		int[] array = {};
		MissingSequentialGenerator.getInstance();
		assertThat(MissingSequentialGenerator.findFirst(array, 1, 1), equalTo(1));
	}
	
	@Test
	public void shouldReturnStartWhenArrayIsNull() throws Exception {
		int[] array = null;
		MissingSequentialGenerator.getInstance();
		assertThat(MissingSequentialGenerator.findFirst(array, 1, 1), equalTo(1));
	}
	
	@Test
	public void shouldThrowAnExceptionWhenStartIsGreaterThanEnd() throws Exception {
		
		this.exception.expect(IllegalArgumentException.class);
		this.exception.expectMessage("Start:10 cannot be greater than End:4");
		int start = 1;
		int[] array = { start};
		MissingSequentialGenerator.getInstance();
		assertThat(MissingSequentialGenerator.findFirst(array, 10, 4), equalTo(2));
	}
	
	@Test
	public void shouldReturnMissingNumberWhenTheNumberIsBetweenTheRange() throws Exception {
		int[] array = {1,2,4,5};
		MissingSequentialGenerator.getInstance();
		assertThat(MissingSequentialGenerator.findFirst(array, 1, 5), equalTo(3));
	}
	
	
	@Test
	public void shouldTestDifferentScenarios() throws Exception {

		assertIntRange(new int[] {0,1,2,3,4, 7},		0, 8,  5);
		assertIntRange(new int[] {}, 					1, 1,  1);
		assertIntRange(new int[] {1}, 				1, 2,  2);
		assertIntRange(new int[] {1}, 				1, 1,  -1);
		assertIntRange(new int[] {}, 					0, 1,  0);
		assertIntRange(new int[] {1, 2}, 				1, 3,  3);
		assertIntRange(new int[] {6, 7, 8, 9}, 		5, 10, 5);
		assertIntRange(new int[] {5, 7, 8, 9}, 		5, 10, 6);
		assertIntRange(new int[] {1, 2, 3, 4}, 		1, 10, 5);
		assertIntRange(new int[] {2, 3, 5}, 			3, 5,  4);
		assertIntRange(new int[] {2, 3, 4}, 			2, 10, 5);
		assertIntRange(new int[] {0,1,2,3,6,7,9,10}, 	6, 10, 8);
		assertIntRange(new int[] {0,1,2,3,6,7,9,10}, 	5, 10, 5);
		assertIntRange(new int[] {1,2,3,6,7,9,10}, 	0, 10, 0);
		
		assertIntRange(new int[] {4, 5, 10, 11}, 		0, 12, 0);
		assertIntRange(new int[] {0, 1, 2, 3}, 		4, 5,  4);
		assertIntRange(new int[] {1, 2, 3,10}, 		4, 11, 4);
		assertIntRange(new int[] {0,1,2,3,4,5,6,7,10}, 0, 8, 8);
		
		
	}
	
	private void assertIntRange(int[] array, int start, int end, int expected) {
		MissingSequentialGenerator.getInstance();
		assertThat(MissingSequentialGenerator.findFirst(
				array, start, end), equalTo(expected));
	}
	
}
