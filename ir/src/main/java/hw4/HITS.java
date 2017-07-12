package hw4;

import hw3.URLTools.Urlnorm;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by Sushant on 7/11/2017.
 */
public class HITS {

    static Set<String> RootSet = new HashSet<>();

    static Set<String> BaseSet = new HashSet<>();

    static  Map<String,HashSet<String>> outlink = new HashMap<>();

    static  Map<String,HashSet<String>> inlink = new HashMap<>();



    private static Client _Tclient = null;


    private static Client getTransportESClient() {
        if (_Tclient == null) {

            try {
//                Settings settings = Settings.builder()
//                        .put("cluster.name", "bazinga").build();
                _Tclient = new PreBuiltTransportClient(Settings.EMPTY)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));


            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return _Tclient;
    }

    private static void updateRoot(String query, String indexName, String type) {

        Client client = null;

        try {

            client = getTransportESClient();
            MatchQueryBuilder qb = matchQuery(
                    "text",
                    query
            );

            SearchResponse scrollResp = client.prepareSearch(indexName)
                    .setTypes(type)
                    .setQuery(qb)
                    .setFetchSource(new String[]{"docno", "out_links", "url"}, null)
                    .setSize(1000)
                    .execute().actionGet();


            for (SearchHit hit : scrollResp.getHits().getHits()) {
                Map map = hit.getSource();

                String docno = Urlnorm.norm(map.get("url").toString()).toLowerCase();

                RootSet.add(docno);


            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null)
                client.close();
            if (_Tclient != null)
                _Tclient = null;
        }


    }

    private static void updateBaseSet() {


    }


    public static void main(String[] args) {

        updateRoot("immigration united states", "mi", "document");
        updateRoot("immigration donald trump", "mi", "document");
        updateRoot("immigration obama", "mi", "document");

        System.out.println(RootSet.size());


        updateBaseSet();


    }

}
