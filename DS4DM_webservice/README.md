#REST service
This project implements a REST service which exposes search and matching functionalities over tabular data.
At the current status there are two implemented functions:
- search (POST), that retrieves matching several potential results for a query table. It returns a collection of candidates tables. For each table it indicates: the name of the table and schema and instance matching with the query table.
- fetch (GET), retrieves a table by name. 


The target data are the two Lucene indexes produced by the [DS4DM Core component](../DS4DM_core).
For a quick test example data is already provide in the [recource folder](resources/smallTestIndex).
If you want to use your own data, produced with the [DS4DM Core component](../DS4DM_core) you can change the values of the properties 
index.location and index.attributesLocation in the [configuration file](testConf.conf).

To run the service with test settings you need to:
- cd into [DS4DM_webservice](DS4DM_webservice)
- run the command activator run (this will start the service on http://localhost:9000)
- perform a search operation, by passing a query table, e.g. with the following:
```
curl --header "Content-type: application/json" --request POST --data '{ "extensionAttributes":[ "population" ], "keyColumnIndex":"0", "maximalNumberOfTables":100, "rankingPolicy":"coverage", "queryTable":[ [ "Country", "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antigua and Barbuda", "Argentina", "Armenia" ], [ "Region", "South Asia", "Eastern Europe & Central Asia", "Middle East & North Africa", "Sub-Saharan Africa", "Latin America & Caribbean", "Latin America & Caribbean", "Eastern Europe & Central Asia", "OECD high income", "OECD high income", "Eastern Europe & Central Asia" ] ] }' http://localhost:9000/search
```



#Examples of calls 

##SEARCH 

> POST *{baseurl}/search*

> **baseurl** = http://ds4dm-experimental.informatik.uni-mannheim.de 


###Request Body:
JSON object with information about the API call. Statements “extensionAttributes” and “queryTable” are mandatory.
```
{  
   "extensionAttributes":[  
      "population"
   ],
   "keyColumnIndex":"0",
   "maximalNumberOfTables":100,
   "rankingPolicy":"coverage",
   "queryTable":[  
      [  
         "Country",
         "Afghanistan",
         "Albania",
         "Algeria",
         "American Samoa",
         "Andorra",
         "Angola",
         "Anguilla",
         "Antigua and Barbuda",
         "Argentina",
         "Armenia"
      ],
      [  
         "Region",
         "South Asia",
         "Eastern Europe & Central Asia",
         "Middle East & North Africa",
         "Sub-Saharan Africa",
         "Latin America & Caribbean",
         "Latin America & Caribbean",
         "Eastern Europe & Central Asia",
         "OECD high income",
         "OECD high income",
         "Eastern Europe & Central Asia"
      ]
   ]
}
```

###Response Body:
JSON object with several relevant tables (an array of arrays, each array representing information about one data table), the target schema, the correspondences at schema level and the correspondences at instance level.
```
{  
   "targetSchema":[  
      "Country",
      "Region",
      "population"
   ],
   "dataTypes":{  
      "Country":"string",
      "Region":"string",
      "population":"string"
   },
   "queryTable2TargetSchema":{  
      "Country":"Country",
      "Region":"Region"
   },
   "extensionAttributes2TargetSchema":{  
      "population":"population"
   },
   "relatedTables":[  
      {  
         "instancesCorrespondences2QueryTable":{  
            "110":{  
               "matching":"2",
               "confidence":0.99
            }
         },
         "tableSchema2TargetSchema":{  
            "4_Country":{  
               "matching":"Country",
               "confidence":0.99
            },
            "2_Population":{  
               "matching":"population",
               "confidence":0.99
            }
         },
         "tableName":"49772588_0_6549847739640234347.json"
      }
   ]
}
```


Returns:
- 200 Ok: Success
- 400 Bad request: Malformed JSON or otherwise wrong request parameter
- 500 Internal server error: Unexpected server error. Probably a bug.



##FETCH TABLE

> GET {baseurl}/fetchTable?name={tableName}

> **baseurl** = http://ds4dm-experimental.informatik.uni-mannheim.de 
 
###Response Body:
JSON object with the requested table and meta data used for data fusion
```
{  
   "dataTypes":{  
      "4_Population":"numeric",
      "0_Name":"string",
      "1_Region":"string",
      "3_GNI":"numeric",
      "2_Income":"string"
   },
   "hasHeader":true,
   "hasKeyColumn":true,
   "headerRowIndex":"0",
   "keyColumnIndex":"0_Name",
   "tableName":"9206866_1_8114610355671172497.json",
   "metaData":{  
      "tableScore":0.0,
      "lastModified":"Feb 26, 2016 11:35:00 AM",
      "URL":"http://www.doingbusiness.org/economycharacteristics",
      "textBeforeTable":"",
      "textAfterTable":"",
      "title":"",
      "coverage":0.0,
      "ratio":0.0,
      "trust":0.0,
      "emptyValues":0.0
   },invi
   "relation":[  
      [  
         "0_Name",
         "Afghanistan",
         "Albania",
         "Algeria"
      ],
      [  
         "1_Region",
         "South Asia",
         "Eastern Europe \u0026 Central Asia",
         "Middle East \u0026 North Africa"
      ],
      [  
         "2_Income",
         "Low income",
         "Upper middle income",
         "Upper middle income"
      ],
      [  
         "3_GNI",
         "517",
         "4,000",
         "4,460"
      ],
      [  
         "4_Population",
         "30,605,401",
         "3,169,087",
         "35,422,589"
      ]
   ]
}
```

Returns:
- 200 Ok: Success
- 400 Bad request: Malformed JSON or otherwise wrong request parameter
- 500 Internal server error: Unexpected server error. Probably a bug.



