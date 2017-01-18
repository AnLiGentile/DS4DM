/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mannheim.uni.ds4dm.utils;

import java.util.ArrayList;
import java.util.List;

import de.mannheim.uni.units.Unit_domi;

/**
 *
 * @author domi
 */
public class Variables {
    
    //determines which string/character corresponds to the null value
    public static String nullValue = "NULL";
    //states which uniqueness rank a column must at least have to be the key column
    //public static double keyUniqueness = 0.5;
    public static double keyUniqueness = 0.3;
    //the cell delimiter
    public static String delimiter = "\",\"";
    //determines whether he cell values are normalized or not
    public static Boolean normalizeValues = true;
    //remove customized stopwords
    public static List<String> stopWords = new ArrayList<>();
    //use unit detection
    public static boolean useUnitDetection = true;
    //Units
    public static List<Unit_domi> units = new ArrayList<>();
    
}
