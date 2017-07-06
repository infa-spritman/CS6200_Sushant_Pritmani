package hw3.MergeWriter;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.BufferedWriter;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by Sushant on 7/3/2017.
 */
public class LinkGraphGenerator {


    private static Client _Tclient = null;


    private static Client getTransportESClient() {
        if (_Tclient == null) {

            try {
                Settings settings = Settings.builder()
                        .put("cluster.name", "bazinga").build();
                _Tclient = new PreBuiltTransportClient(settings)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));


            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return _Tclient;
    }

    public static void generate(String dir, String indexName, String type){
        Client client = null;
        BufferedWriter bw = null;
        java.io.FileWriter fw = null;
        int count =0;
        String finalGraphPath = dir + "linkgraph.txt";
        try {
            File file = new File(finalGraphPath);

            if (!file.exists()) {
                file.createNewFile();
            }

            fw = new java.io.FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            client = getTransportESClient();
            SearchResponse scrollResp = client.prepareSearch(indexName)
                    .setTypes(type)
                    .setFetchSource(new String[]{"docno","out_links"}, null)
                    .setScroll(new TimeValue(60000))
                    .setSize(10000)
                    .execute().actionGet();
            do {
                for (SearchHit hit : scrollResp.getHits().getHits()) {
                    Map map = hit.getSource();
//                    if(map.get("author").equals("Sushant"))
//                        count = count +1;
//
//                    System.out.println(count);
                    StringBuilder sb = new StringBuilder();
                    sb.append(map.get("docno")+ " ");
                    sb.append(map.get("out_links") + "\n");

                    bw.write(sb.toString());


                }

                scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            }
            while (scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null)
                client.close();
            if(_Tclient!=null)
                _Tclient.close();
        }

    }



    public static void main(String[] args){

            generate("C:\\Users\\Sushant\\Desktop\\IR\\ResultAssignment3\\","mi","document");

    }

}
