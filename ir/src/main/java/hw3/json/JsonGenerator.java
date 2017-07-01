package hw3.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hw1.parser.JsoupParser;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sushant on 5/12/2017.
 */
public class JsonGenerator {

    public static String getJsonObject(Element docElement, String type, String indexName, Map<String, String> headers, LinkedList<String> rel_links, Integer depth, String originalURL, String normalisedURL,String title) {
        StringBuilder jsonObject = new StringBuilder("");

        jsonObject.append(dataJsonString(docElement, headers, rel_links, depth, originalURL, normalisedURL,title));
        jsonObject.append("\n");


        return jsonObject.toString();
    }

    private static String metaJsonString(String indexName, String type, String docno) {
        return "{\"index\":{\"_index\":\"" + indexName + "\", \"_type\":\"" + type + "\", \"_id\":\"" + docno + "\"}}";

    }

    private static String dataJsonString(Element doc, Map<String, String> headers, LinkedList<String> rel_links, Integer depth, String originalURL, String normalisedURL, String title) {

        DocumentModel d = createDocumentObject(doc, headers, rel_links, depth, originalURL, normalisedURL,title);
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;
        try {


            // Convert object to JSON string
            jsonString = mapper.writeValueAsString(d);
            //System.out.println(jsonString);


        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return jsonString;
        }
    }

    public static DocumentModel createDocumentObject(Element doc, Map<String, String> headers, LinkedList<String> rel_links, Integer depth, String originalURL, String normalisedURL,String title) {

        DocumentModel db = new DocumentModel();

        db.setDocno(normalisedURL);
        db.setHTTPheader(headers.toString());
        db.setTitle(title);
        db.setHtml_Source(doc.html());
        db.setOut_links(rel_links);
        db.setText(doc.getElementsByTag("html").text());
        db.setUrl(originalURL);
        db.setAuthor("Sushant");
        db.setDepth(depth);

        return db;
    }
}
