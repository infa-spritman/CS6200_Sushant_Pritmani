package hw7.indexer;

import hw1.elasticAPI.BulkIndex;
import hw7.json.JsonGenerator;
import org.elasticsearch.client.Response;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Sushant on 8/8/2017.
 */
public class IndexRunner {

    public static void main(String[] args) {

        String dir = "C://Users//Sushant//Desktop//IR//data//trec07p";
        HashMap<String, String> labelMap = getLabelMap(dir + "//full//index");
        System.out.println(labelMap.size());
        writeFiles("classifier-email-1", "test", "ham", labelMap, dir + "//data");

//        File[] files = new File(dir).listFiles();
//        for(File f : files){
//            if(f.isFile() && !f.getName().equalsIgnoreCase("readme")){
//                Elements doc = JsoupParser.getTagFromDoc(JsoupParser.parseDoc(f.getAbsolutePath()),"DOC");
//                String jsonFile = JsonGenerator.getJsonObject(doc,"hw1", "ap_dataset");
//                Response fileResponse = BulkIndex.createIndexRequest(jsonFile, "");
//                System.out.println(fileResponse.getStatusLine().toString());
//
//
//            }
//
//        }

    }

    private static void writeFiles(String index_name, String type, String lable, HashMap<String, String> labelMap, String data_path) {

        AtomicInteger bulkCount = new AtomicInteger(0);
        StringBuilder sb = new StringBuilder();

        labelMap.entrySet()
                .stream()
                .filter(map -> map.getValue().equals(lable))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())).
                forEach((doc, lb) -> {
                    int i = bulkCount.get();
                    String body = null;
                    try {
                        body = new String(Files.readAllBytes(Paths.get(data_path+"//"+doc)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(i % 5 == 0 && i!=0)
                        sb.append(JsonGenerator.getJsonObject(index_name,type,doc,lable,body,"test"));
                    else
                        sb.append(JsonGenerator.getJsonObject(index_name,type,doc,lable,body,"train"));

                    if ( i % 5000 == 0 && i!=0) {
                        sb.setLength(sb.length()-1);
                        Response fileResponse = BulkIndex.createIndexRequest(sb.toString(), index_name);
                        System.out.println(fileResponse.getStatusLine().toString());
                        sb.setLength(0);
                    }

                    bulkCount.incrementAndGet();
                });

        Response fileResponse = BulkIndex.createIndexRequest(sb.toString(), index_name);
        System.out.println(fileResponse.getStatusLine().toString());
        sb.setLength(0);
    }

    private static HashMap<String, String> getLabelMap(String labelFile) {
        HashMap<String, String> tempMap = new HashMap<>();

        try (Stream<String> lines = Files.lines(Paths.get(labelFile), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                String[] split = line.split(" ");
                String[] split1 = split[1].split("/");
                tempMap.put(split1[2].trim(), split[0]);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempMap;

    }


}
