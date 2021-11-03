package org.andreidodu.tbot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

//	@Deprecated
//	public static List<String> asList(String str) {
//		return asList(str, ",");
//	}

	public static List<String> asList(String str, String div) {
		List<String> result = new ArrayList<>();
		if (str != null) {
			String[] arr = str.split(div);
			if (arr.length == 1 && "".equals(arr[0])) {
				return result;
			}
			return Arrays.asList(arr);
		}
		return result;
	}
}
