/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mannheim.uni.ds4dm.tableprocessor;

import de.mannheim.uni.ds4dm.model.TableColumn.ColumnDataType;
import de.mannheim.uni.units.Unit_domi;


/**
 *
 * @author domi
 */
public class ColumnType {
    
    private ColumnDataType type;
    private Unit_domi unit;
    
    public ColumnType(ColumnDataType type, Unit_domi unit) {
        this.type = type;
        this.unit = unit;
    }

    /**
     * @return the type
     */
    public ColumnDataType getType() {
        return type;
    }

    /**
     * @return the unit
     */
    public Unit_domi getUnit() {
        return unit;
    }
    
}
