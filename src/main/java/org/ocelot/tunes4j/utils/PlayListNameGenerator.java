package org.ocelot.tunes4j.utils;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.ArrayUtils.toPrimitive;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.Lists;

public class PlayListNameGenerator {
	
	private static PlayListNameGenerator generator = new PlayListNameGenerator();

	private PlayListNameGenerator() {
	}

	public static PlayListNameGenerator getInstance() {
		return generator;
	}

	public String findNext(String[] names) {
		
		if(ArrayUtils.isEmpty(names)) {
			return "Playlist 1";
		}
		
		List<Integer> list = Lists.newArrayList();
		for(String name : names) {
			int number = extractNumber(name);
			if(number > -1) {
				list.add(number);
			}
		}

		if (isNotEmpty(list)) {
			Collections.sort(list);
			int[] numbers = toPrimitive(list.toArray(new Integer[list.size()]));
			int generatedIndex = extractNextNumber(numbers);
			return "Playlist " + generatedIndex;
		}
		return "Playlist 1";
	}

	private int extractNextNumber(int[] numbers) {
		return MissingSequentialGenerator.getInstance().findFirst(numbers, 1, numbers[numbers.length -1] + 1);
	}

	private int extractNumber(String name) {
		String extractedName = extractPlaylistName(name);
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(extractedName);
		int n = -1;
		if (m.find()) {
			n = Integer.parseInt(m.group());
		}
		return n;
	}
	
	private String extractPlaylistName(String name) {
		Pattern p = Pattern.compile("[Pp][Ll][Aa][Yy][Ll][Ii][Ss][Tt](\\s+)[0-9]+");
		Matcher m = p.matcher(name);
		if (m.find()) {
			return m.group();
		}
		return "";
	}


}
