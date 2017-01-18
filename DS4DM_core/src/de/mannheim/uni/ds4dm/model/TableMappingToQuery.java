package de.mannheim.uni.ds4dm.model;


public class TableMappingToQuery extends TableMapping{

//    {  
//        "instancesCorrespondences2QueryTable":{  
//           "3":"44",
//           "6":"49"
//        },
//        "tableName":"8009320_0_5507755752653817624",
//        "tableSchema2TargetSchema":{  
//           "0":"0",
//           "2":"3"
//        }
//     }
     
     
    private String[] targetSchema;
//    private String tableName;
//    private int numHeaderRows;
//    private Pair<String, Double> mappedClass;
//    private HashMap<Integer, Pair<String, Double>> mappedProperties = new HashMap<>();
//    private HashMap<Integer, Pair<String, Double>> mappedInstances = new HashMap<>();
//    private int keyIndex;
//    private HashMap<Integer, ColumnDataType> dataTypes = new HashMap<>();
    

	public String[] getTargetSchema() {
		return targetSchema;
	}

	public void setTargetSchema(String[] targetSchema) {
		this.targetSchema = targetSchema;
	}

	public void setMappedInstancesToQuery(Object object) {
		// TODO Auto-generated method stub
		
	}

	public void setMappedSchemaToQuery(Object object) {
		// TODO Auto-generated method stub
		
	}
    
    
    
    
    
//	tm.setMappedInstancesToQuery(mappedInstances);
//	tm.setMappedSchemaToQuery(mappedSchema);
//	schemaMapping.setTargetSchema(ts);
//	schemaMapping.setMappedInstancesToQuery(null);
//	schemaMapping.setMappedSchemaToQuery(null);
	
	
	
}
