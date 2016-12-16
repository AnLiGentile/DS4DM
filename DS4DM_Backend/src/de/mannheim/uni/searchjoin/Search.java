package de.mannheim.uni.searchjoin;

import com.google.common.collect.Lists;
import de.mannheim.uni.datafusion.DataFuser;
import de.mannheim.uni.model.JoinResult;
import de.mannheim.uni.model.Table;
import de.mannheim.uni.model.TableColumn;
import de.mannheim.uni.pipelines.Pipeline;
import org.codehaus.jettison.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * Created by steffen on 16.12.16.
 */
public class Search {
    private Pipeline pipeline;

    public static void main(String[] args) {
        Search search = new Search();
        try {
            search.searchJoin("/home/steffen/workspace/DS4DM_gs/resources/GS_domi/context_json_url","1146722_1_7558140036342906956.json");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Search() {    }

    public Search(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void searchJoin(String filePath, String queryTableFileName) throws ParseException, JSONException, IOException {
        Table table = createTableFromFile(new File(filePath+"/"+queryTableFileName));
        TreeSet<String> tables = new TreeSet<>();

        Map<String, JoinResult> searchJoinResults = new HashMap<String, JoinResult>();
        DataFuser fuser = new DataFuser(pipeline);
//        Table fusedTable = fuser.fuseQueryTableWithEntityTables(table, results, null);
    }

    private Table createTableFromFile(File file) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject o = (JSONObject) parser.parse(new FileReader(file));

        long subjectColumnIdx = (long) o.get("keyColumnIndex");
        JSONArray jsonTable = (JSONArray) o.get("relation");
        Iterator<?> iter = jsonTable.iterator();
        Table table = new Table();
        while(iter.hasNext()) {
            JSONArray column = (JSONArray) iter.next();
            TableColumn tableColumn = new TableColumn();
            Iterator<?> iterator = column.iterator();
            tableColumn.setHeader((String)iterator.next());
            int row = 1;
            while(iterator.hasNext()) {
                tableColumn.addNewValue(row, (String)iterator.next(),true);
                row++;
            }
            table.addColumn(tableColumn);
        }
        return table;
    }

    private List<String> getSubjectsFromJson(File document)
            throws IOException, ParseException, FileNotFoundException, JSONException {
        JSONParser parser = new JSONParser();
        JSONObject o = (JSONObject) parser.parse(new FileReader(document));

        long subjectColumnIdx = (long) o.get("keyColumnIndex");
        JSONArray table = (JSONArray) ((JSONArray) o.get("relation")).get((int) subjectColumnIdx);
        Iterator<?> iterator = table.iterator();
        List<String> subjects = Lists.newArrayList();
        while (iterator.hasNext()) {
            subjects.add((String) iterator.next());
        }
        return subjects;
    }

    public void searchLuceneBoolean() {

    }

//    private List<String> getTopNdocs(TopDocs top) throws IOException {
//        DirectoryReader dirReader = DirectoryReader.open(dir);
//        IndexSearcher searcher = new IndexSearcher(dirReader);
//        List<String> docs = Lists.newArrayList();
//        for(ScoreDoc score : top.scoreDocs){
//            docs.add(searcher.doc(score.doc).getField("tableId").stringValue());
//        }
//        return docs;
//    }

//    public Map<String,TopDocs> queryTables(int numDocs) throws IOException {
//        Map<String,TopDocs> topDocsMap = Maps.newConcurrentMap();
//        DirectoryReader dirReader = DirectoryReader.open(dir);
//        IndexSearcher searcher = new IndexSearcher(dirReader);
//        DocIdSetIterator dit = DocIdSetIterator.all(dirReader.maxDoc());
//        int i = 0;
//        while ((i = dit.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
//            BooleanQuery  query = new BooleanQuery();
//            Terms terms = dirReader.getTermVector(i, "contents");
//            TermsEnum iter = terms.iterator();
//            BytesRef b;
//            while((b = iter.next()) != null) {
//                TermQuery t = new TermQuery(new Term("contents",b));
//                query.add(new BooleanClause(t, BooleanClause.Occur.SHOULD));
//            }
//            TopDocs top = searcher.search(query,numDocs); // return numDocs top tables
//            IndexableField id = dirReader.document(i).getField("tableId");
//            topDocsMap.put(id.stringValue(), top);
//        }
//        return topDocsMap;
//    }
}
