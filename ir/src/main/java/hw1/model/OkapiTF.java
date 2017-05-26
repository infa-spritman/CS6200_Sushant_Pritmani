package hw1.model;

import hw1.FIleProcessor.ResultFileWriter;
import hw1.elasticAPI.QueryHandler;
import hw1.queryProcessor.QueryFormatter;
import javafx.util.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by Sushant on 5/20/2017.
 */
public class OkapiTF {

    public static void runOkapi(String queryPath, String reportPath, String indexName, String type) {
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
                        .addScriptField("okapi-tf", new Script(ScriptType.INLINE, "groovy",
                                "int d = doc['doclength'].value; double tf = _index['text'][\"" + term + "\"].tf(); double df = _index['text'][\"" + term + "\"].df();return tf/(tf+0.5+1.5*(d/441.6));",
                                Collections.emptyMap()))
                        .setScroll(new TimeValue(60000))
                        .setSize(10000)
                        .execute().actionGet();
                do {
                    for (SearchHit hit : scrollResp.getHits().getHits()) {
                        //Map map = hit.getSource();
                        //System.out.println(hit.getId() + ":" +hit.getFields().get("okapi-tf").getValues().get(0));

                        String docID = hit.getId();
                        double score = Double.parseDouble(hit.getFields().get("okapi-tf").getValues().get(0).toString());
                        if (scoreMap.containsKey(docID))
                            scoreMap.put(docID, scoreMap.get(docID) + score);
                        else
                            scoreMap.put(docID, score);

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

    private static double calculateScore(String docStats, String term, double avgDocLength, Integer docLength) {

        Pair<Integer, Integer> tfdf = QueryHandler.getTFDF(docStats, term);
        Integer tf = tfdf.getKey();
        double score = tf / (tf + 0.5 + 1.5 * (docLength / avgDocLength));
        return score;
    }




//    private static <K, V extends Comparable<? super V>> Map<K, V> sortMap(Map<K, V> map) {
//        return map.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        (e1, e2) -> e1,
//                        LinkedHashMap::new
//                ));
//    }

    public static void main(String[] args) {

        Map<String, Double> items = new HashMap<>();
        items.put("A", 200.0);
        items.put("B", 20.0);
        items.put("C", 300.0);
        items.put("D", 230.0);
        items.put("E", 150.0);
        items.put("F", 60.0);


//        Client client = null;
//        Map<String, Integer> docMap = new HashMap<String, Integer>();
//        try {
//            client = new PreBuiltTransportClient(Settings.EMPTY)
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//            MatchQueryBuilder qb = matchQuery(
//                    "text",
//                    "document"
//            );
//            SearchResponse scrollResp = client.prepareSearch("ap_dataset")
//                    .setTypes("hw1").setQuery(qb)
//                    .addScriptField("okapi-tf", new Script(ScriptType.INLINE, "groovy",
//                            "int d = doc['doclength'].value;double tf = _index['text']['document'].tf();double df = _index['text']['document'].df();return tf/(tf+0.5+1.5*(d/441.6));",
//                            Collections.emptyMap()))
//                    .setScroll(new TimeValue(60000))
//                    .setSize(4000)
//                    .execute().actionGet();
//            do {
//                for (SearchHit hit : scrollResp.getHits().getHits()) {
//                    //Map map = hit.getSource();
//                    System.out.println(hit.getId() + ":" + hit.getFields().get("okapi-tf").getValues().get(0));
//
//                }
//                System.out.println("Preparing for another hit");
//                scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
//            }
//            while (scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } finally {
//            if (client != null)
//                client.close();
//        }


        runOkapi("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\fs.txt", "ap_dataset", "hw1");

        //        iDs.forEach((docID, docLength) -> {
//            String docStats = QueryHandler.getDocStats("ap_dataset", "hw1", docID, "text");
//
//            System.out.println("score_map_length" + scoreMap.size());
//            double score =0 ;
//            for(String term: split){
//                //System.out.println(term);
//                score += calculateScore(docStats,term,avgDocLength,docLength);
//            }
//            if(score>0)
//                scoreMap.put(docID,score);
//
//
//            });

//        Map<String, Double> scoreMap = new HashMap<>();
//        for (String term : split) {
//            System.out.println(">>>>Term :" + term);
//            iDs.forEach((docID, docLength) -> {
//                //System.out.println(docID);
//                Response docStats = QueryHandler.getDocStats("ap_dataset", "hw1", docID, "text");
//                System.out.println("score_map_length" + scoreMap.size());
//                double score= calculateScore(docStats,term,avgDocLength,docLength);
//                if(scoreMap.containsKey(docID))
//                    scoreMap.put(docID,scoreMap.get(docID)+score);
//                else
//                    scoreMap.put(docID,score);
//            });
//        }


    }
}
