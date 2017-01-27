package de.mannheim.uni.datafusion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import de.mannheim.uni.model.DataType.ColumnDataType;
import de.mannheim.uni.model.Table;
import de.mannheim.uni.model.TableColumn;
import de.mannheim.uni.pipelines.Pipeline;
import de.mannheim.uni.statistics.Timer;
import de.mannheim.uni.units.SubUnit;
import de.mannheim.uni.units.Unit;
import de.mannheim.uni.utils.PipelineConfig;
import de.mannheim.uni.utils.concurrent.Parallel;
import de.mannheim.uni.utils.concurrent.Parallel.Consumer;

/**
 * @author petar
 * 
 */
public class TableDataCleaner {
	private Table table;
	private Pipeline pipeline;

	public TableDataCleaner(Table table, Pipeline pipeline) {
		this.pipeline = pipeline;
		this.table = table;

	}


	private void normalizeUnits(Table table) {
		for (TableColumn column : table.getColumns()) {
			if (column.getDataType() == ColumnDataType.unit
					&& column.getBaseUnit() != null
					&& !column.getBaseUnit().getName().equals("Currency")) {
				normalizeColumnUnit(column);

			} else if (column.getDataType() == ColumnDataType.numeric) {
				normalizeColumnNumeric(column);
			}
		}

	}

	/**
	 * Cleans the numeric values of spaces and characters
	 * 
	 * @param column
	 */
	private void normalizeColumnNumeric(TableColumn column) {
		for (Entry<Integer, String> entry : column.getValues().entrySet()) {
			if (entry.getValue()
					.equalsIgnoreCase(PipelineConfig.NULL_VALUE))
				continue;
			String newValue = entry.getValue().replaceAll("[^\\d.]", "");

			// remove multiple dots
			if (newValue.matches(".*\\..*\\..*"))
				newValue = newValue.replaceAll("\\.", "");
			column.getValues().put(entry.getKey(), newValue);
		}

	}

	/**
	 * normalizes units to
	 * 
	 * @param column
	 */
	private void normalizeColumnUnit(TableColumn column) {
		Unit baseUnit = column.getBaseUnit();
		for (Entry<Integer, String> entry : column.getValues().entrySet()) {
			SubUnit subUnit = column.getUnits().get(entry.getKey());
			if (subUnit != null && subUnit.getNewValue() != null)
				column.getValues().put(entry.getKey(), subUnit.getNewValue());
		}
		try {
			if (baseUnit != null) {
				column.setCleaningInfo("Unit was normalized to "
						+ baseUnit.getName() + " using unit "
						+ baseUnit.getMainUnit().getAbbrevations().get(0));
				pipeline.getLogger().info(
						"Unit was normalized to "
								+ baseUnit.getName()
								+ " using unit "
								+ baseUnit.getMainUnit().getAbbrevations()
										.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();// the base unt might be null
		}

	}

	/**
	 * removes columns with column density higher than the config density
	 * 
	 * @param fusedTable
	 */
	public Table filterColumnsByColumnDensity(Table fusedTable) {
		if(pipeline.getDataFusionColumnDensity()==1)
			return fusedTable;
		
		Timer t = new Timer("Filter columns by density");
		List<TableColumn> colsToremove = new ArrayList<TableColumn>();
		for (TableColumn column : fusedTable.getColumns()) {
			double columnDensity = 0.0;
			if (column.getValuesInfo().containsKey(
					PipelineConfig.NULL_VALUE)) {
				columnDensity = ((double) (column.getValuesInfo().get(
						PipelineConfig.NULL_VALUE) / (double) column
						.getTotalSize()));
			} else {
				int totalNulls = 0;
				for (String value : column.getValues().values()) {
					if (value.toLowerCase().equals(
							PipelineConfig.NULL_VALUE))
						totalNulls++;
				}
				columnDensity = (double) ((double) totalNulls / (double) column
						.getValues().size());
			}
			if (columnDensity > pipeline.getDataFusionColumnDensity()) {
				colsToremove.add(column);
				pipeline.getLogger().info(
						"Column removed because of NULL values: "
								+ column.getHeader() + "(density is "
								+ columnDensity + ")");
			}
		}
		for (TableColumn c : colsToremove) {
			fusedTable.getColumns().remove(c);
		}
		t.stop();
		return fusedTable;
	}

	/**
	 * removes rows with row density higher than the config density
	 * 
	 * @param fusedTable
	 */
	public Table filterRowsByRowDensity(final Table fusedTable) {
		if(pipeline.getDataFusionRowDensity()==1)
			return fusedTable;
		
		Timer t = new Timer("Filter rows by density");
		
		//final List<Integer> rowsToremove = Collections.synchronizedList(new LinkedList<Integer>());
		final ConcurrentLinkedQueue<Integer> rowsToRemove = new ConcurrentLinkedQueue<Integer>();
		
		int numRows = 0;
		for(TableColumn c : fusedTable.getColumns())
			if(c.isKey())
			{
				numRows = c.getValues().size();
				break;
			}
		//int numRows = fusedTable.getCompaundKeyColumns().get(0).getValues().size();
		
		try {
			Parallel.forLoop(pipeline.getInstanceStartIndex(), numRows + pipeline.getInstanceStartIndex(), new Consumer<Integer>() {

				public void execute(Integer parameter) {
					int row = parameter;
					double density = 0.0;
					
					for(TableColumn c : fusedTable.getColumns())
						if(!c.isKey() && (!c.getValues().containsKey(row) || c.getValues().get(row).equalsIgnoreCase(PipelineConfig.NULL_VALUE)))
							density++;
					
					density = density / ((double)fusedTable.getColumns().size()-1);
					
					if (density > pipeline.getDataFusionRowDensity()) {
						rowsToRemove.add(row);
						pipeline.getLogger().info(
								"Row removed because of NULL values: "
										+ row + "(density is "
										+ density + ")");
					}
					
				}
			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*for(int row = pipeline.getInstanceStartIndex(); row<numRows + pipeline.getInstanceStartIndex(); row++) {
			double density = 0.0;
			
			for(TableColumn c : fusedTable.getColumns())
				if(!c.isKey() && (!c.getValues().containsKey(row) || c.getValues().get(row).equalsIgnoreCase(PipelineConfig.NULL_VALUE)))
					density++;
			
			density = density / ((double)fusedTable.getColumns().size()-1);
			
			if (density > pipeline.getDataFusionRowDensity()) {
				rowsToremove.add(row);
				pipeline.getLogger().info(
						"Row removed because of NULL values: "
								+ row + "(density is "
								+ density + ")");
			}
		}*/
		int removed = rowsToRemove.size();
		for (Integer row : rowsToRemove) {
			for(TableColumn c : fusedTable.getColumns())
				c.getValues().remove(row);
		}
		
		pipeline.getLogger().info("Removed " + removed + "/" + numRows + " low density rows");
		
		t.stop();
		return fusedTable;
	}
	
	public Table removeNullRows(final Table fusedTable, final TableColumn referenceColumn) {
		Timer t = new Timer("Remove null rows");
		
		pipeline.getLogger().log(Level.INFO, "Removing null rows ...");
		
		//final List<Integer> rowsToremove = Collections.synchronizedList(new LinkedList<Integer>());
		final ConcurrentLinkedQueue<Integer> rowsToRemove  = new ConcurrentLinkedQueue<Integer>();
		
		int numRows = 0;
		for(TableColumn c : fusedTable.getColumns())
			if(c.isKey())
			{
				numRows = c.getValues().size();
				break;
			}
		//int numRows = fusedTable.getCompaundKeyColumns().get(0).getValues().size();

		try {
			new Parallel<Integer>().foreach(fusedTable.getFirstKey().getValues().keySet(), new Consumer<Integer>() {
			//Parallel.forLoop(pipeline.getInstanceStartIndex(), numRows + pipeline.getInstanceStartIndex(), new Consumer<Integer>() {

				public void execute(Integer parameter) {
					int row = parameter;
					boolean hasValue=false;
					for(TableColumn c : fusedTable.getColumns())
						if(!c.isKey() 
								&& c!=referenceColumn 
								&& c.getValues().containsKey(row) 
								&& !c.getValues().get(row).equalsIgnoreCase(PipelineConfig.NULL_VALUE) 
								&& !c.getValues().get(row).equals(""))
						{
							hasValue=true;
							break;
						}
					
					if(!hasValue)
					{
						rowsToRemove.add(row);
					}
				}
			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*for(int row = pipeline.getInstanceStartIndex(); row<numRows + pipeline.getInstanceStartIndex(); row++) {
			boolean hasValue=false;
			for(TableColumn c : fusedTable.getColumns())
				if(!c.isKey() && c!=referenceColumn && c.getValues().containsKey(row) && !c.getValues().get(row).equalsIgnoreCase(PipelineConfig.NULL_VALUE))
				{
					hasValue=true;
					break;
				}
			
			if(!hasValue)
			{
				rowsToremove.add(row);
			}
		}*/
		int toRemove = rowsToRemove.size();
		for (Integer row : rowsToRemove) {
			for(TableColumn c : fusedTable.getColumns())
				c.getValues().remove(row);
		}
		
		pipeline.getLogger().log(Level.INFO, "Removed "+ toRemove + "/" + numRows + " null rows ...");
		
		t.stop();
		return fusedTable;
	}
	
//	public static void main(String[] args) {
//		NGramTokenizer tok = new NGramTokenizer(2, 4, true,
//				new SimpleTokenizer(true, true));
//		Jaccard sim = new Jaccard(tok);
//		System.out.println(sim.score("River", "rdf-schema#label"));
//	}
}
