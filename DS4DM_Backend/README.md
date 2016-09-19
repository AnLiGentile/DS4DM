#Backend core 

The Backend core encompasses two macro functionalities:

-data indexing (TableIndex)
-data searching (TableSearch)

##TableIndex
Table Indexing
We currently support the indexing of:

our normalized table format (as resulting from the preprocessing step)
csv tables
Dresden University proprietary format - for the sake of enabling result comparison with a similar research project
Other formats will be supported adding conversion functionalities to the preprocessing component. The indexing process can be run offline as a batch process. There are currently three separate entry points for the supported formats (respectively for our standardised table format, for csv and for Dresden University format). 
They all produce two index structures for the collection of tables:

an IndexOfHeaders, storing every item of the table headers
an IndexOfValues, stroring every cell of the tables
In the indexing step we perform data type detection for each column, using manually defined regular expressions to detect string, numeric value, date and link. 

##TableSearch
The TableSearch component incorporates searching and integration facilities. TableSearch receives as input a query table, where the user indicates:

the subject column - the column in the table that contains entity names
a set of extraction attributes - that she wants to extend her table with
The TableSearch process:

retrieves a set of relevant tables from the connected indexes of tables. 
In the current prototype the search is a Keyword Based Search performed on the IndexOfHeaders.
identifies correspondences between relevant tables and the query table at:
schema level (which columns in each retrieved table correspond to which columns in the query table)
instance level (which rows in each retrieved table correspond to which rows the query table)
In the current prototype this is implemented as a String matching process.
As output TableSearch returns all the identified correspondences and a confidence score for each matching.

