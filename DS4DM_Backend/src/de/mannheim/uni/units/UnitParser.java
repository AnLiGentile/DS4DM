package de.mannheim.uni.units;

/**
 * @author petar
 *
 */
public class UnitParser {

    UnitManager mgr;

    public UnitParser() {
        mgr = new UnitManager();
    }

    public SubUnit parseUnit(String text) {
        //Long startTime = System.nanoTime();
        text = text.replaceAll(" ", "");
        try {
            SubUnit sub = mgr.parseUnit(text);
            if (sub != null && !sub.getName().equals("normalized number")) {
                return sub;
            }
         //Long time = System.nanoTime()-startTime;   
        // System.out.println(text  + " parsing: " + time);
        } catch (Exception e) {
        }
        return null;
    }
}
