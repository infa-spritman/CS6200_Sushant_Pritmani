package hw4;

import hw3.URLTools.Urlnorm;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by Sushant on 7/11/2017.
 */
public class LinkGraphOutlinkGenerator {


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

    public static void generate(String dir, String indexName, String type) {
        Map<String, Set<String>> outlink = new HashMap<>();

        Client client = null;
        BufferedWriter bw = null;
        java.io.FileWriter fw = null;
        int count = 0;
        String finalGraphPath = dir + "out-linkgraph_deli.txt";
        try {

            client = getTransportESClient();
            SearchResponse scrollResp = client.prepareSearch(indexName)
                    .setTypes(type)
                    .setFetchSource(new String[]{"docno", "out_links", "url"}, null)
                    .setScroll(new TimeValue(60000))
                    .setSize(10000)
                    .execute().actionGet();
            do {
                for (SearchHit hit : scrollResp.getHits().getHits()) {
                    Map map = hit.getSource();

                    String docno = Urlnorm.norm(map.get("url").toString()).toLowerCase();

                    if (!docno.equals("invalid_url")) {

                        List<String> out_links = (List<String>) map.get("out_links");
                        HashSet temp = new HashSet();

                        out_links.stream().forEach(ol -> {

                            String normOutlink = Urlnorm.norm(ol).toLowerCase();

                            if (!normOutlink.equals("invalid_url")) {

                                temp.add(normOutlink);
                            }


                        });

                        if (outlink.containsKey(docno)) {
                            Set<String> strings = outlink.get(docno);
                            strings.addAll(temp);
                            outlink.put(docno, strings);

                        } else {

                            outlink.put(docno, temp);
                        }


                    }

                }
                System.out.println("Preparing for another...");
                scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            }
            while (scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.

            _Tclient.close();

            File file = new File(finalGraphPath);

            if (!file.exists()) {
                file.createNewFile();
            }

            fw = new java.io.FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            BufferedWriter finalBw = bw;
            outlink.forEach((docId, outlinkSet) -> {
                StringBuilder sb = new StringBuilder();
                sb.append(docId + "<:>");
                outlinkSet.stream().forEach(ot -> {
                    sb.append(ot + "<:>");
                });

                sb.append("\n");
                try {
                    finalBw.write(sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null)
                client.close();
            if (_Tclient != null)
                _Tclient.close();
        }

    }


    public static void main(String[] args) {

        generate("C:\\Users\\Sushant\\Desktop\\IR\\ResultAssignment3\\", "mi", "document");

    }


}
