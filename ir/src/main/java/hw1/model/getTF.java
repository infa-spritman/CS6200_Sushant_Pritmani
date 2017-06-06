package hw1.model;

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
import java.util.Collections;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * Created by Sushant on 5/31/2017.
 */
public class getTF {


    public static void main(String[] args){

        Client client = null;
        String term = "algorithm";
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            MatchQueryBuilder qb = matchQuery(
                    "text",
                    term
            );
            SearchResponse scrollResp = client.prepareSearch("ap_dataset")
                    .setTypes("hw1").setQuery(qb)
                    .addScriptField("tf", new Script(ScriptType.INLINE, "groovy",
                            "int d = doc['doclength'].value; double tf = _index['text'][\"" + term + "\"].tf(); double df = _index['text'][\"" + term + "\"].df();return tf;",
                            Collections.emptyMap()))
                    .setScroll(new TimeValue(60000))
                    .setSize(10000)
                    .execute().actionGet();
            do {
                for (SearchHit hit : scrollResp.getHits().getHits()) {
                    //Map map = hit.getSource();
                    //System.out.println(hit.getId() + ":" +hit.getFields().get("okapi-tf").getValues().get(0));

                    String docID = hit.getId();
                    double tf = Double.parseDouble(hit.getFields().get("tf").getValues().get(0).toString());
                    System.out.println(docID + " " + tf);

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
}
