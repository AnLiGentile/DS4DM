#Backend core 

The Backend core encompasses two macro functionalities:
- data indexing (TableIndex)
- data searching (TableSearch)

##TableIndex
We currently support the indexing of:

- our normalized table format (as resulting from the preprocessing step)
- csv tables
- Dresden University proprietary format - for the sake of enabling result comparison with a similar research project
Other formats will be supported adding conversion functionalities to the preprocessing component. The indexing process can be run offline as a batch process. There are currently three separate entry points for the supported formats (respectively for our standardised table format, for csv and for Dresden University format). 

They all produce two index structures for the collection of tables:

- an IndexOfHeaders, storing every item of the table headers
- an IndexOfValues, stroring every cell of the tables

In the indexing step we perform data type detection for each column, using manually defined regular expressions to detect string, numeric value, date and link. 

> To generate an index starting from json tables (the output of the [DS4DM_Preprocessing](../DS4DM_Preprocessing) component) you can run:

> java -jar ds4dm_backend-0.0.1-SNAPSHOT-jar-with-dependencies.jar indexing.conf test_indexing resources/json/articles web

Where:
> [indexing.conf](indexing.conf) is a configuration file where you can define the location of the generated indexes

> [test_indexing] is a label for the process

> [resources/json/articles](resources) is the folder containing the json tables to index

##TableSearch
The TableSearch component incorporates searching and integration facilities. TableSearch receives as input a query table, where the user indicates:

- the subject column - the column in the table that contains entity names
- a set of extraction attributes - that she wants to extend her table with

The TableSearch process:

- retrieves a set of relevant tables from the connected indexes of tables. 
In the current prototype the search is a Keyword Based Search performed on the IndexOfHeaders.
- identifies correspondences between relevant tables and the query table (currently implemented as a String matching process) at:
 - schema level (which columns in each retrieved table correspond to which columns in the query table)
 - instance level (which rows in each retrieved table correspond to which rows the query table)


As output TableSearch returns all the identified correspondences and a confidence score for each matching.

> TableSearch fuctionalities are currently exposed as [DS4DM_webservice](../DS4DM_webservice).

