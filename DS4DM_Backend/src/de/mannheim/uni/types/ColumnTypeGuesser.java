package de.mannheim.uni.types;

import java.util.Date;

import de.mannheim.uni.parsers.DateUtil;
import de.mannheim.uni.parsers.GeoCoordinateParser;
import de.mannheim.uni.parsers.NumericParser;
import de.mannheim.uni.parsers.URLParser;
import de.mannheim.uni.parsers.UnitParser;
import de.mannheim.uni.units.SubUnit;

/**
 * this class is from the indivual project TypeGuesser
 * @author petar
 * 
 */
public class ColumnTypeGuesser {
	public static enum ColumnDataType {
		numeric, string, coordinate, date, link, bool, unknown, unit, list
	};

	private UnitParser unitParser;

	public ColumnTypeGuesser() {
		unitParser = new UnitParser();
	}

	/**
	 * use for rough type guesssing
	 * 
	 * @param columnValue
	 *            is the value of the column
	 * @param columnHeader
	 *            is the header of the column, often contains units
	 *            abbreviations
	 * @param useUnit
	 *            the typeGuesser will try to find units
	 * @param unit
	 *            the returning unit (if found)
	 * @return
	 */
	public ColumnDataType guessTypeForValue(String columnValue,
			String columnHeader, boolean useUnit, SubUnit unit) {

		if (columnValue.matches("^\\{.+\\|.+\\}$"))
			return ColumnDataType.list;

		if (useUnit) {

			SubUnit unitS = null;
			if (columnHeader != null) {

				unitS = unitParser.parseUnit(columnValue + " "
						+ extractUnitAbbrFromHeader(columnHeader));
			}
			if (unitS == null) {
				unitS = unitParser.parseUnit(columnValue);
			}
			if (unitS != null) {
				unit.setAbbrevations(unitS.getAbbrevations());
				unit.setBaseUnit(unitS.getBaseUnit());
				unit.setConvertible(unitS.isConvertible());
				unit.setName(unitS.getName());
				unit.setNewValue(unitS.getNewValue());
				unit.setRateToConvert(unitS.getRateToConvert());
				return ColumnDataType.unit;
			}
		}

		try {
			Date date = DateUtil.parse(columnValue);
			if (date != null)
				return ColumnDataType.date;
		} catch (Exception e) {

		}

		if (Boolean.parseBoolean(columnValue))
			return ColumnDataType.bool;

		if (URLParser.parseURL(columnValue))
			return ColumnDataType.link;

		if (GeoCoordinateParser.parseGeoCoordinate(columnValue))
			return ColumnDataType.coordinate;

		if (NumericParser.parseNumeric(columnValue)) {
			return ColumnDataType.numeric;
		}

		return ColumnDataType.string;

	}

	/**
	 * Returns the value from brackets
	 * 
	 * @param header
	 * @return
	 */
	private static String extractUnitAbbrFromHeader(String header) {
		if (header.matches(".*\\(.*\\).*"))
			return header.substring(header.indexOf("(") + 1,
					header.indexOf(")"));

		return header;
	}

	public static void main(String[] args) {
		// initialize the type guesser
		ColumnTypeGuesser g = new ColumnTypeGuesser();

		// list
		System.out.println(g.guessTypeForValue("{value1|value2}", null, false,
				null));

		// string
		System.out.println(g.guessTypeForValue("Andorra", null, false,
				null));
		
		// numeric
		System.out.println(g.guessTypeForValue("1,256", null, false, null));
		System.out.println(g.guessTypeForValue("1,256.05", null, false, null));

		// date
		System.out
				.println(g.guessTypeForValue("january 12", null, false, null));

		// coordinate
		System.out.println(g.guessTypeForValue("41.1775 20.6788", null, false,
				null));

		// guess units
		SubUnit subUnit = new SubUnit();
		g.guessTypeForValue("3000", "area (sq km)", true, subUnit);

		String baseUnit = subUnit.getBaseUnit().getName();
		String normalizedValue = subUnit.getNewValue();
		System.out.println("3000 area (sq km) was converted to: "
				+ normalizedValue + " "
				+ subUnit.getBaseUnit().getMainUnit().getName() + " "
				+ baseUnit);

	}

}
