package hw1.json;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Sushant on 5/12/2017.
 */
public class JsonGenerator {

    public static String getJsonObject(Elements docElements,String type, String indexName){
        StringBuilder jsonObject = new StringBuilder("");
        for(Element e: docElements){
            jsonObject.append(metaJsonString(indexName,type,e.getElementsByTag("DOCNO").text()));
        }

        return jsonObject.toString();
    }

    private static String metaJsonString(String indexName, String type, String docno) {
    }
}
