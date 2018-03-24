package org.ocelot.tunes4j.utils;

import java.util.ArrayList;
import java.util.List;

public class Test3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		List<Integer> list = new ArrayList<Integer>(0);
		list.add(new Integer("3"));
		list.add(new Integer("6"));
		list.add(new Integer("9"));
		list.add(new Integer("12"));
		list.add(new Integer("18"));
		list.add(new Integer("21"));
		list.add(new Integer("24"));
		list.add(new Integer("27"));
		
		printBugFizz(list);

	}

	public static void printBugFizz(List<Integer> integers) { 
		for(Integer item : integers) {
	        if(item%2==0) System.out.println("Bug");
	        if(item%3==0) System.out.println("Fizz");
	        if(item%2==0 && item%3==0) System.out.println("Bug Fizz");
		}
	}

}
