#REST service
This project implements a REST service which exposes search and matching functionalities over tabular data.
The target data are the two Lucene indexes produced by the [DS4DM Core component](../DS4DM_core).
For a quick test example data is already provide in the [recource folder](resources/smallTestIndex).
If you want to use your own data, produced with the [DS4DM Core component](../DS4DM_core) you can change the values of the properties 
index.location and index.attributesLocation in the [configuration file](testConf.conf).

To run the service with test settings you need to:
- cd into [DS4DM_webservice](DS4DM_webservice)
- run the command activator run (this will start the service on http://localhost:9000)
- perform a search operation, by passing a query table
```
curl --header "Content-type: application/json" --request POST --data '{ "extensionAttributes":[ "population" ], "keyColumnIndex":"0", "maximalNumberOfTables":100, "rankingPolicy":"coverage", "queryTable":[ [ "Country", "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antigua and Barbuda", "Argentina", "Armenia" ], [ "Region", "South Asia", "Eastern Europe & Central Asia", "Middle East & North Africa", "Sub-Saharan Africa", "Latin America & Caribbean", "Latin America & Caribbean", "Eastern Europe & Central Asia", "OECD high income", "OECD high income", "Eastern Europe & Central Asia" ] ] }' http://localhost:9000/search
```
