package de.mannheim.uni.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * @author annalisa
 *
 */
public class MapUtil {
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Map.Entry<K, V>> st = map.entrySet().stream();

		st.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

		return result;
	}

		
}