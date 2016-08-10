# DS4DM Preprocessing

The preprocessing component takes care of extracting tables from various input data and produces a standard representation for each extracted table.

It is currently implemented as a batch process. We assume that input data is stored locally, e.g. in the form of HTML Web pages.

The [process](./src/main/java/de/mannheim/uni/ds4dm/preprocessing/html/LocalWebTableExtractorFromFolder.java) iterates over the locally stored pages, extracts useful tables and represent the output in a standardised format. 

The extraction is performed with [BasicExtraction algorithm](./src/main/java/org/webdatacommons/webtables/extraction/BasicExtractionAlgorithm.java) which iterates through tables in the HTML page using the “table” tag. Heuristics are used to discard noisy tables:

- tables inside forms
- tables which contain sub-tables
- tables with less than a certain number of rows (this parameter is currently set to 3)
- tables with less than a certain number of columns (this parameter is currently set to 2)
- tables with "rowspan" or "colspan"
- tables that do not contain header cells (“th” element)

The remaining tables are classified as “layout” or “content” tables. The classification is done with the classifyTable method. It uses two models ([SimpleCart_P1](./src/main/resources/SimpleCart_P1.mdl) and [SimpleCart_P2](./src/main/resources/SimpleCart_P2.mdl)). The first model identifies if the table is a LAYOUT table (only used for visualization formatting purposes). If this is not the case, then the second model is used to classify a table as:

- RELATION (containing multiple entities and relations)
- ENTITY (describing a single entity)
- MATRIX
- OTHER (if no type can be decided)

For all retained tables, the method additionally identifies:

- the key column
- the header row
- context information

The [key column detection](./src/main/java/org/webdatacommons/webtables/extraction/detection/KeyColumnDetection.java) selects the column with the maximal number of unique values. In case of a tie, the left-most column is used. 
The [header detection](./src/main/java/org/webdatacommons/webtables/extraction/detection/HeaderDetection.java) identifies a row which has a different content pattern for the majority of its cells, with respect to the other rows. Currently this test is performed only on the first non-empty row of the table against the others. 
As context information for each table we select 200 characters before and 200 after the table itself.
