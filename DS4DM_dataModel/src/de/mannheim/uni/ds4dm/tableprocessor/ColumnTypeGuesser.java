package de.mannheim.uni.ds4dm.tableprocessor;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.mannheim.uni.ds4dm.model.TableColumn.ColumnDataType;
import de.mannheim.uni.unit.parsers.DateUtil;
import de.mannheim.uni.unit.parsers.GeoCoordinateParser;
import de.mannheim.uni.unit.parsers.NumericParser;
import de.mannheim.uni.unit.parsers.URLParser;
import de.mannheim.uni.units.UnitParserDomi;
import de.mannheim.uni.units.Unit_domi;
import de.mannheim.uni.utils.Variables;

/**
 * @author petar
 *
 */
public class ColumnTypeGuesser {
    
    private static Pattern listCharactersPattern = Pattern.compile("\\{|\\}");
    
    /**
     * use for rough type guesssing
     *
     * @param columnValue is the value of the column
     * @param columnHeader is the header of the column, often contains units
     * abbreviations
     * @param useUnit the typeGuesser will try to find units
     * @param unit the returning unit (if found)
     * @return
     */
    public ColumnType guessTypeForValue(String columnValue, Unit_domi headerUnit) {
        if (checkIfList(columnValue)) {
            List<String> columnValues;
//            columnValue = columnValue.replace("{", "");
//            columnValue = columnValue.replace("}", "");
            columnValue = listCharactersPattern.matcher(columnValue).replaceAll("");
            columnValues = Arrays.asList(columnValue.split("\\|"));
            Map<ColumnDataType, Integer> countTypes = new HashMap<>();
            Map<Unit_domi, Integer> countUnits = new HashMap<>();
            for(String singleValue : columnValues) {
                ColumnType guessedSingleType = guessTypeForSingleValue(singleValue, headerUnit);
                
                Integer cnt = countTypes.get(guessedSingleType.getType());
                if(cnt==null) {
                    cnt = 0;
                }
                countTypes.put(guessedSingleType.getType(), cnt+1);
//                if(countTypes.containsKey(guessedSingleType.getType())) {
//                    countTypes.put(guessedSingleType.getType(), countTypes.get(guessedSingleType.getType())+1);
//                }
//                else {
//                    countTypes.put(guessedSingleType.getType(), 1);
//                }
                
                cnt = countUnits.get(guessedSingleType.getUnit());
                if(cnt==null) {
                    cnt = 0;
                }
                countUnits.put(guessedSingleType.getUnit(), cnt+1);
//                    countUnits.put(guessedSingleType.getUnit(), countUnits.get(guessedSingleType.getUnit())+1);
//                }
//                else {
//                    countUnits.put(guessedSingleType.getUnit(), 1);
//                }
            }
            int max = 0;
ColumnDataType finalType = null;
            for(ColumnDataType type : countTypes.keySet()) {
                if(countTypes.get(type)>max) {
                    max = countTypes.get(type);
                    finalType = type;
                }
            }
            max = 0;
            Unit_domi finalUnit = null;
            for(Unit_domi type : countUnits.keySet()) {
                if(countUnits.get(type)>max) {
                    max = countUnits.get(type);
                    finalUnit = type;
                }
            }
            return new ColumnType(finalType, finalUnit);
        }
        else {
            return guessTypeForSingleValue(columnValue, headerUnit);
        }
    }
    
    private static Pattern listPattern = Pattern.compile("^\\{.+\\|.+\\}$");
    
    private boolean checkIfList(String columnValue) {
//        if (columnValue.matches("^\\{.+\\|.+\\}$")) {
        if (listPattern.matcher(columnValue).matches()) {
            return true;
        }
        return false;
    }
    
    private ColumnType guessTypeForSingleValue(String columnValue, Unit_domi headerUnit) {
        // check the length
        boolean validLenght = true;
        if (columnValue.length() > 50) {
            validLenght = false;
        }
        if (validLenght && Boolean.parseBoolean(columnValue)) {
            return new ColumnType(ColumnDataType.bool,null);
        }
        if (URLParser.parseURL(columnValue)) {
            return new ColumnType(ColumnDataType.link,null);
        }
        if (validLenght && GeoCoordinateParser.parseGeoCoordinate(columnValue)) {
            return new ColumnType(ColumnDataType.coordinate,null);
        }        
        if (validLenght) {
            try {
                Date date = DateUtil.parse(columnValue);
                if (date != null) {
                    return new ColumnType(ColumnDataType.date,null);
                }
            } catch (Exception e) {
            }
        }
        if (validLenght && NumericParser.parseNumeric(columnValue)) {        
            if(Variables.useUnitDetection) {
                Unit_domi unit = headerUnit;
                if(headerUnit==null) {
                    unit = UnitParserDomi.checkUnit(columnValue);
                }
                return new ColumnType(ColumnDataType.unit,unit);
            }
            else {
                return new ColumnType(ColumnDataType.numeric,null);
            }
        }        
        return new ColumnType(ColumnDataType.string, null);
    }
}
