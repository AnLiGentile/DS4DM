//package de.mannheim.uni.ds4dm.tableprocessor;
//
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//import java.util.regex.Pattern;
//
//import de.mannheim.uni.TableProcessor.TableManager;
//import de.mannheim.uni.ds4dm.model.Table;
//import de.mannheim.uni.ds4dm.model.TableColumn;
//import de.mannheim.uni.utils.ValueComparator;
//import de.mannheim.uni.utils.Variables;
//
///**
// * Detects the key column of the table.
// *
// * @author petar
// *
// */
//public class TableKeyIdentifier {
//
//    public void indenfityLODKeys(Table table) {
//
//        identifyKeys(table);
//    }
//    
//    private static final Pattern prefLabelPattern = Pattern.compile("([^#]*#)?([a-z]{1,9})?prefLabel$");
//    private static final Pattern namePattern =Pattern.compile("([^#]*#)?name$");
//    private static final Pattern labelPattern = Pattern.compile("([^#]*#)?([a-z]{1,9})?label$");
//    private static final Pattern titlePattern = Pattern.compile("([^#]*#)?([a-z]{1,9})?title$");
//    private static final Pattern labelPattern2 =Pattern.compile("([^#]*#)?.*Label$");
//    private static final Pattern namePattern2 = Pattern.compile("([^#]*#)?.*Name$");
//    private static final Pattern titlePattern2 = Pattern.compile("([^#]*#)?.*Title$");
//    private static final Pattern alternateNamePattern = Pattern.compile("([^#]*#)?([a-z]{1,9})?alternateName$");
//    
//    public void identifyKeys(Table table) {
//        TableColumn key = null;
//        int keyColumnIndex = -1;
//        List<Double> columnUniqueness = new ArrayList<>(table.getColumns().size());
//
//        for (int i=table.getColumns().size()-1; i>=0; i--) {
//            TableColumn column = table.getColumns().get(i);
////            System.out.println("header: " + column.getHeader());
////            System.out.println("dt: " + column.getDataType());
//            if (column.getDataType() != TableColumn.ColumnDataType.string) {
//                continue;
//            }
//            if (prefLabelPattern.matcher(column.getHeader()).matches()) {
//                key = column;
//                break;
//            }
//            if (namePattern.matcher(column.getHeader()).matches()) {
//                key = column;
//                break;
//            }
//            if (labelPattern.matcher(column.getHeader()).matches()) {
//                key = column;
//            }
//
//            if (titlePattern.matcher(column.getHeader()).matches()) {
//                key = column;
//            }
//            if (labelPattern2.matcher(column.getHeader()).matches()) {
//                key = column;
//            }
//
//            if (namePattern2.matcher(column.getHeader()).matches()) {
//                key = column;
//            }
//
//            if (titlePattern2.matcher(column.getHeader()).matches()) {
//                key = column;
//            }
//            if (alternateNamePattern.matcher(column.getHeader()).matches()) {
//                key = column;
//            }
//
//        }
//        if (key != null) {
//            keyColumnIndex = table.getColumns().indexOf(key);
//            if (table.getColumns().get(keyColumnIndex).getColumnUniqnessRank() >= Variables.keyUniqueness
//                    && table.getColumns().get(keyColumnIndex).getColumnStatistic().getAverageValueLength() > 3.5
//                    && table.getColumns().get(keyColumnIndex).getColumnStatistic().getAverageValueLength() <= 200) {
//                table.getColumns().get(keyColumnIndex).setKey(true);
//                table.setHasKey(true);
//                return;
//            }
//            //the found key does not fit the requirements, see if another column does
//            key = null;
//        }
//
//        for (TableColumn column : table.getColumns()) {
//            columnUniqueness.add(column.getColumnUniqnessRank());
//        }
//
//        if (columnUniqueness.isEmpty()) {
//            return;
//        }
//        double maxCount = -1;
//        int maxColumn = -1;
//
//        for (int i = 0; i < columnUniqueness.size(); i++) {
//            if (columnUniqueness.get(i) > maxCount && table.getColumns().get(i).getDataType() == TableColumn.ColumnDataType.string
//                    && table.getColumns().get(i).getColumnStatistic().getAverageValueLength() > 3.5
//                    && table.getColumns().get(i).getColumnStatistic().getAverageValueLength() <= 200) {
//                maxCount = (Double) columnUniqueness.get(i);
//                maxColumn = i;
//            }
//        }
//
//        if (key == null) {
//            if (maxColumn == -1) {
//                table.setHasKey(false);
//                return;
//            }
//            key = table.getColumns().get(maxColumn);
//        }
//        keyColumnIndex = table.getColumns().indexOf(key);
//
//        if (columnUniqueness.get(keyColumnIndex) < Variables.keyUniqueness) {
//            table.setHasKey(false);
//            return;
//        }
//
//        table.getColumns().get(keyColumnIndex).setKey(true);
//        table.setHasKey(true);
//    }
//    
//
//
//	private Pipeline pipeline;
//
//	public TableKeyIdentifier(Pipeline pipeline) {
//		this.pipeline = pipeline;
//	}
//
//	public Map<String, Double> identifyKey(Table table) {
//		Map<String, Double> columnUniqueness = new HashMap<String, Double>();
//
//		switch (pipeline.getKeyidentificationType()) {
//		case none:
//			columnUniqueness = null;
//			break;
//
//		case single:
//			columnUniqueness = identifyKeysNaive(table,
//					pipeline.isRemoveNonStrings());
//			break;
//		case singleWithRefineAttrs:
//			columnUniqueness = identifyKeysNaive(table,
//					pipeline.isRemoveNonStrings());
//			break;
//
//		case compaund:
//			// identifyCompaundKey(table);
//			break;
//
//		}
//		return columnUniqueness;
//	}
//
//	private Map<String, Double> identifyKeysNaive(Table table,
//			boolean removeNonStrings) {
//		List<TableColumn> keys = new ArrayList<TableColumn>();
//		Map<String, Double> columnUniqueness = new LinkedHashMap<String, Double>();
//		// check if there is a common key
//		boolean isKeySet = false;
//		for (TableColumn column : table.getColumns()) {
//			if (column.getHeader().contains("#label")) {
//				column.setKey(true);
//				isKeySet = true;
//				keys.add(column);
//				break;
//			}
//		}
//		if (!isKeySet) {
//
//			for (TableColumn column : table.getColumns()) {
//				if (column.getHeader().contains("#name")
//						|| column.getHeader().contains("name")
//						|| column.getHeader().contains("label")) {
//					if (column.getHeader().contains("_label"))
//						continue;
//					column.setKey(true);
//					isKeySet = true;
//					keys.add(column);
//					break;
//				}
//			}
//		}
//		long start = System.currentTimeMillis();
//
//		List<TableColumn> keyColumns = new ArrayList<TableColumn>();
//		// remove non string columns
//		if (removeNonStrings)
//			TableManager.removeNonStringColumns(table);
//
//		int i = 0;
//		for (TableColumn column : table.getColumns()) {
//			if (column.getAverageValuesSize() > pipeline
//					.getAverageKeyValuesLimitMin()
//					&& column.getAverageValuesSize() <= pipeline
//							.getAverageKeyValuesLimitMax()) {
//				if (pipeline.getKeyidentificationType() == KeyIdentificationType.singleWithRefineAttrs)
//
//				{
//					if (column.getDataType() == TableColumn.ColumnDataType.string)
//						columnUniqueness.put(Integer.toString(i),
//								column.getColumnUniqnessRank());
//				} else {
//					columnUniqueness.put(Integer.toString(i),
//							column.getColumnUniqnessRank());
//				}
//
//			}
//			i++;
//		}
//		if (columnUniqueness.size() == 0)
//			return null;
//		double maxCount = columnUniqueness.values().iterator().next();
//		String maxColumn = columnUniqueness.keySet().iterator().next();
//		for (Entry<String, Double> entry : columnUniqueness.entrySet()) {
//			if (entry.getValue() > maxCount) {
//				maxCount = (Double) entry.getValue();
//				maxColumn = entry.getKey();
//			}
//		}
//
//		if (!isKeySet)
//			table.getColumns().get(Integer.parseInt(maxColumn)).setKey(true);
//
//		if (!isKeySet)
//			keys.add(table.getColumns().get(Integer.parseInt(maxColumn)));
//		if (keys.size() > 0) {
//			table.setCompaundKeyColumns(keys);
//		} else {
//			table.setHasKey(false);
//		}
//
//		// no key was found
//		if (maxCount < pipeline.getKeyUniqueness()) {
//			if (!pipeline.isSilent())
//				pipeline.getLogger()
//						.info("The table"
//								+ table.getFullPath()
//								+ " doesn't have a Key! decrease the key uniqueness limit to find another key!");
//			table.setHasKey(false);
//			return columnUniqueness;
//		}
//
//		if (!pipeline.isSilent()) {
//			pipeline.getLogger().info(
//					"The key column is " + keys.get(0).getHeader()
//							+ " with uniqueness of " + maxCount);
//
//			long end = System.currentTimeMillis();
//			pipeline.getLogger().info(
//					"Time for single key identification: "
//							+ ((double) (end - start) / 1000));
//		}
//		return columnUniqueness;
//	}
//
//	private boolean checkIfKey(String colId, Table table) {
//		TableColumn column = table.getColumns().get(Integer.parseInt(colId));
//		if (column.getColumnUniqnessRank() >= pipeline.getKeyUniqueness())
//			return true;
//		double frac = 0.0;
//		if (column.getValuesInfo().containsKey(PipelineConfig.NULL_VALUE))
//			frac = ((double) (column.getValuesInfo().get(
//					PipelineConfig.NULL_VALUE) / (double) column.getTotalSize()));
//		if (column.getColumnUniqnessRankWoNull() >= pipeline.getKeyUniqueness()
//				&& frac <= pipeline.getKeyNullValuesFraction())
//			return true;
//		return false;
//	}
//
//
//
//	public static Map<String, Double> sortMap(Map<String, Double> inputMap) {
//
//		ValueComparator bvc = new ValueComparator(inputMap);
//		TreeMap<String, Double> sortedRankedResults = new TreeMap<String, Double>(
//				bvc);
//		sortedRankedResults.putAll(inputMap);
//
//		return sortedRankedResults;
//	}
//
//}
