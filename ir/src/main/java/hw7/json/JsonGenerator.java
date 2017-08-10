package hw7.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Sushant on 5/12/2017.
 */
public class JsonGenerator {

    public static String getJsonObject(String index_name, String type, String file_name, String lable, String body, String split) {
        StringBuilder jsonObject = new StringBuilder("");

        jsonObject.append(metaJsonString(index_name, type, file_name));
        jsonObject.append("\n");

        jsonObject.append(dataJsonString(file_name, lable, body, split));
        jsonObject.append("\n");


        return jsonObject.toString();
    }

    private static String metaJsonString(String indexName, String type, String docno) {
        return "{\"index\":{\"_index\":\"" + indexName + "\", \"_type\":\"" + type + "\", \"_id\":\"" + docno + "\"}}";

    }

    private static String dataJsonString(String file_name, String lable, String body, String split) {

        DocumentModel d = createDocumentObject(file_name, lable, body, split);
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

    public static DocumentModel createDocumentObject(String file_name, String lable, String body, String split) {

        DocumentModel db = new DocumentModel();

        db.setBody(body);
        db.setFile_name(file_name);
        db.setLabel(lable);
        db.setSplit(split);


        return db;
    }
}
