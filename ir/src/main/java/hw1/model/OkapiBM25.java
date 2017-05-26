package hw1.model;

import hw1.FIleProcessor.ResultFileWriter;
import hw1.elasticAPI.QueryHandler;
import hw1.queryProcessor.QueryFormatter;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * Created by Sushant on 5/24/2017.
 */
public class OkapiBM25 {


    public static void runOkapiBM25(String queryPath, String reportPath, String indexName, String type) {
        Map<Integer, ArrayList<String>> refinedQueries = QueryFormatter.getRefinedQueries(queryPath, indexName);
        System.out.println("queries size" + refinedQueries.size());
//        Map<String, Integer> iDs = QueryHandler.getIDs(indexName, type);
//        System.out.println("Ids size" + iDs.size());
        double avgDocLength = QueryHandler.getAvgDocLength(indexName, type);
        refinedQueries.forEach((k, v) -> {
            System.out.println("Query No:" + k);

            Map<String, Double> search = search(v, avgDocLength, indexName, type);
            ResultFileWriter.writeTofile(reportPath, k, search, 1000);
        });

    }


    private static Map<String, Double> search(ArrayList<String> queryTerms, double avgDocLength, String indexName, String type) {

        Map<String, Double> scoreMap = new HashMap<>();
        Client client = null;


        for (String term : queryTerms) {
            //System.out.println("term" + term);
            try {
                client = new PreBuiltTransportClient(Settings.EMPTY)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
                MatchQueryBuilder qb = matchQuery(
                        "text",
                        term
                );
                SearchResponse scrollResp = client.prepareSearch("ap_dataset")
                        .setTypes("hw1").setQuery(qb)
                        .addScriptField("okapi25", new Script(ScriptType.INLINE, "groovy",
                                "int d = doc['doclength'].value; double tf = _index['text'][\"" + term + "\"].tf(); double df = _index['text'][\"" + term + "\"].df();double okapi = (tf+ tf*1.2)/(tf+ (1.2*(0.25+ 0.75*(d/441.6)))); return (Math.log10(84678.5/(df+0.5)))*okapi;",
                                Collections.emptyMap()))
                        .setScroll(new TimeValue(60000))
                        .setSize(10000)
                        .execute().actionGet();
                do {
                    for (SearchHit hit : scrollResp.getHits().getHits()) {
                        //Map map = hit.getSource();
                        //System.out.println(hit.getId() + ":" +hit.getFields().get("okapi-tf").getValues().get(0));
                        int wordCOunt = Collections.frequency(queryTerms, term);
                        double queryFactor = (wordCOunt*101)/(100+wordCOunt);
                        String docID = hit.getId();
                        double score = Double.parseDouble(hit.getFields().get("okapi25").getValues().get(0).toString());
                        if (scoreMap.containsKey(docID))
                            scoreMap.put(docID, scoreMap.get(docID) + (score*queryFactor));
                        else
                            scoreMap.put(docID, (score*queryFactor));

                    }
                    System.out.println("Preparing for another hit");
                    scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
                }
                while (scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } finally {
                if (client != null)
                    client.close();
            }
        }

        return scoreMap;
    }

    public static void main(String[] args){

        runOkapiBM25("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\fs.txt", "ap_dataset", "hw1");


    }
}