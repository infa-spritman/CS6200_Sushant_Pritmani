package hw1.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hw1.parser.JsoupParser;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sushant on 5/12/2017.
 */
public class JsonGenerator {

    public static String getJsonObject(Elements docElements, String type, String indexName) {
        StringBuilder jsonObject = new StringBuilder("");
        for (Element e : docElements) {
            jsonObject.append(metaJsonString(indexName, type, e.getElementsByTag("DOCNO").text()));
            jsonObject.append("\n");
            jsonObject.append(dataJsonString(e));
            jsonObject.append("\n");

        }

        return jsonObject.toString();
    }

    private static String metaJsonString(String indexName, String type, String docno) {
        return "{\"index\":{\"_index\":\"" + indexName + "\", \"_type\":\"" + type + "\", \"_id\":\"" + docno + "\"}}";

    }

    private static String dataJsonString(Element doc) {
        DocumentModel d = createDocumentObject(doc);
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

    private static DocumentModel createDocumentObject(Element doc) {

        DocumentModel db = new DocumentModel();

        db.setDocno(JsoupParser.getTagFromElement(doc,"DOCNO").text());
        db.setFileID(JsoupParser.getTagFromElement(doc,"FILEID").text());
        db.setFirst(JsoupParser.getTagFromElement(doc,"FIRST").text());
        db.setSecond(JsoupParser.getTagFromElement(doc,"SECOND").text());
        db.setDateline(JsoupParser.getTagFromElement(doc,"DATELINE").text());
        String text = JsoupParser.getTagFromElement(doc,"TEXT").text();
        db.setText(text);

        List<String> headList = new ArrayList<String>();
        Elements tempHead = JsoupParser.getTagFromElement(doc,"HEAD");
        for(Element h : tempHead)
            headList.add(h.text());

        db.setHead(headList);

        List<String> byLineList = new ArrayList<String>();
        Elements tempByline = JsoupParser.getTagFromElement(doc,"BYLINE");
        for(Element by : tempByline)
            byLineList.add(by.text());

        db.setByline(byLineList);

        db.setDoclength(text.split(" ").length);

        return db;
    }
}
