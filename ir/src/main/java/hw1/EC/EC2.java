package hw1.EC;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hw1.FIleProcessor.ExcelFileWriter;
import hw1.FIleProcessor.ResultFileWriter;
import hw1.elasticAPI.QueryHandler;
import hw1.queryProcessor.QueryFormatter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sushant on 5/26/2017.
 */
public class EC2 {

    public static void runSigniFicantAgg(String queryPath, String reportPath, String indexName, String type) {
        Map<Integer, ArrayList<String>> refinedQueries = QueryFormatter.getRefinedQueries(queryPath, indexName);
        System.out.println("queries size" + refinedQueries.size());
        refinedQueries.forEach((k, v) -> {
            System.out.println("Query No:" + k);
            Map<String, ArrayList<String>> significantTerms = significantAgg(v, indexName, type);
            ExcelFileWriter.wrtiteToExcel(reportPath,k,significantTerms);
        });

    }

    private static Map<String, ArrayList<String>> significantAgg(ArrayList<String> terms, String indexName, String type) {

        Map<String, ArrayList<String>> significantTermsMap = new HashMap<>();
        RestClient restClient = null;

        for (String term : terms) {
            Response significantAggResponse = null;
            String responseEntity = "";
            try {
                restClient = RestClient.builder(
                        new HttpHost("localhost", 9200, "http")).build();

                String json = "{\n" +
                        "    \"query\" : {\n" +
                        "       \"terms\" : {\"text\" : [ \"" + term + "\" ]}\n" +
                        "    },\n" +
                        "    \"aggregations\" : {\n" +
                        "        \"significantTypes\" : {\n" +
                        "            \"significant_terms\" : {\n" +
                        "              \"field\" : \"text\"              \n" +
                        "            }\n" +
                        "        }\n" +
                        "    },\n" +
                        "    \"size\": 0\n" +
                        "}";

                HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);

                String endPoint = "/" + indexName + "/" + type + "/_search?filter_path=aggregations.significantTypes.buckets.key,aggregations.significantTypes.buckets.bg_count";

                significantAggResponse = restClient.performRequest("GET", endPoint
                        ,
                        Collections.singletonMap("pretty", "true"), entity);

                responseEntity = EntityUtils.toString(significantAggResponse.getEntity());
                ArrayList<String> significantTerms = parseResponse(responseEntity);
//                ArrayList<String> finalSigniFicantListWithIDF = new ArrayList<>();
//                for(String signiTerm : significantTerms){
//
//                    double idf  = calculateIDF(signiTerm);
//
//                    finalSigniFicantListWithIDF.add(signiTerm+ " : "+ idf);
//                }
                significantTermsMap.put(term, significantTerms);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error doing Significant Terms Agg...");
            } finally {
                try {
                    restClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }
        return significantTermsMap;
    }

    private static double calculateIDF(String signiTerm) {
        return 0;
    }

    private static ArrayList<String> parseResponse(String responseEntity) {
        ArrayList<String> parsedResponse = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            final JsonNode jsonNode = mapper.readTree(responseEntity);
            JsonNode buckets = jsonNode.get("aggregations").get("significantTypes").get("buckets");

            for (JsonNode js : buckets) {

                String key = js.get("key").toString();
                Double idf = Math.log10(84678.0 / Double.parseDouble(js.get("bg_count").toString()));
                parsedResponse.add(key + " : " + idf);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return parsedResponse;
    }

    public static void main(String[] args) {

        runSigniFicantAgg("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\sig.xlsx", "ap_dataset", "hw1");

    }
}
