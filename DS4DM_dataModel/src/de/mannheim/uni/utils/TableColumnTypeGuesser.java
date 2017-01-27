package de.mannheim.uni.utils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import de.mannheim.uni.types.ColumnTypeGuesser;
import de.mannheim.uni.types.ColumnTypeGuesser.ColumnDataType;
import de.mannheim.uni.units.SubUnit;

public class TableColumnTypeGuesser {

	
	ColumnTypeGuesser g = new ColumnTypeGuesser();
	
	public ColumnDataType guessTypeForColumn(List<String> columnValues,
			String columnHeader, boolean useUnit, SubUnit unit){
		
		SortedMap<ColumnDataType, Integer> votes = new TreeMap<ColumnDataType, Integer>();
		//TODO randomly select 30 objects, do no iterate on full column
		for (String s:columnValues){
			ColumnDataType t =ColumnDataType.string;
			
			if (s != null)
				t =	this.g.guessTypeForValue(s, columnHeader, useUnit, unit);


				
			if (votes.containsKey(t))
				votes.put(t, votes.get(t)+1);
			else
				votes.put(t, 1);

		}
				return votes.firstKey();
		
	}
			
			
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
