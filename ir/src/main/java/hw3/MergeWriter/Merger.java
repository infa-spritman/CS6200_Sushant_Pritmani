package hw3.MergeWriter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.script.Script;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by Sushant on 6/29/2017.
 */
public class Merger {

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

    public static String getAuthor(String indexName, String type, String docNo, String JSON) {
        Client client = null;

        try {
            client = getTransportESClient();

            GetResponse response = client.prepareGet(indexName, type, docNo)
                    .setFetchSource(new String[]{"docno", "author"}, null)
                    .get();


            if (response.isExists()) {

                System.out.println("\nTrying to merge DocNo: " + docNo);

                String newAuthor = response.getSource().get("author") + " Sushant";

                UpdateRequest updateRequest = new UpdateRequest(indexName, type, docNo)
                        .script(new Script("ctx._source.author = \"" + newAuthor + "\"")).timeout("5000ms");

                UpdateResponse updateResponse = client.update(updateRequest).get();

                if(updateResponse.status().getStatus()==200)
                    System.out.println( "Merged Successfully, DocNo: " + docNo);
                else
                    System.out.println("Failed to Merge, DocNo: " + docNo);



            } else {

                System.out.println("\nTrying to insert DocNo: " + docNo);
                IndexResponse indexResponse = client.prepareIndex(indexName, type,docNo)
                        .setSource(JSON)
                        .get();
//                System.out.println(indexResponse.status());
//                System.out.println(indexResponse.status().getStatus());
                if(indexResponse.status().getStatus()==201)
                    System.out.println( "Indexed Successfully, DocNo: " + docNo);
                else
                    System.out.println("Failed to Index, DocNo: " + docNo);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return null;
    }


    public static void main(String[] args) {

        //getAuthor("test", "document", "0", "");
        //getAuthor("test", "document", "1", "");

        Set<String> visited = new HashSet<>();

        String filePath = "C:\\Users\\Sushant\\Desktop\\IR\\ResultAssignment3\\data.txt";

        ObjectMapper mapper = new ObjectMapper();


        try (Stream<String> lines = Files.lines(Paths.get(filePath), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {

                Map<String, Object> map = new HashMap<String, Object>();


                // convert JSON string to Map
                try {
                   // map = mapper.readValue(line, new TypeReference<Map<String, String>>(){});
                    final JsonNode jsonNode = mapper.readTree(line);
//                    String docno = jsonNode.get("docno").toString();
//                    String docno1 = jsonNode.get("docno").asText();
                    String normURl  = jsonNode.get("docno").asText().toLowerCase();

                    if(!visited.contains(normURl)) {

                        getAuthor("test", "document", jsonNode.get("docno").asText(), line);
                        visited.add(normURl);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (_Tclient != null)
                _Tclient.close();

            System.out.println(visited.size());
        }

    }

}
