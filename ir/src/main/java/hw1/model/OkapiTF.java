package hw1.model;

import hw1.elasticAPI.QueryHandler;
import hw1.queryProcessor.QueryFormatter;
import javafx.util.Pair;
import org.elasticsearch.client.Response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sushant on 5/20/2017.
 */
public class OkapiTF {

    public static void runOkapi(String queryPath, String reportPath,String indexName, String type) {
        Map<Integer, String> refinedQueries = QueryFormatter.getRefinedQueries(queryPath);
        System.out.println("queries size" + refinedQueries.size());
        Map<String, Integer> iDs = QueryHandler.getIDs(indexName, type);
        System.out.println("Ids size" + iDs.size());
        double avgDocLength = QueryHandler.getAvgDocLength(indexName, type);
        refinedQueries.forEach((k, v) -> {
            System.out.println("Query No:" + k);
            Map<String, Double> search = search(v, iDs, avgDocLength);
            writeTofile(reportPath, k,search , 100);
        });

    }

    private static Map<String, Double> search(String query, Map<String, Integer> iDs, double avgDocLength) {
        String[] split = query.split(" ");
        System.out.println("spilit size"+split.length);
        Map<String, Double> scoreMap = new HashMap<>();
        iDs.forEach((docID, docLength) -> {
            String docStats = QueryHandler.getDocStats("ap_dataset", "hw1", docID, "text");

            System.out.println("score_map_length" + scoreMap.size());
            double score =0 ;
            for(String term: split){
                //System.out.println(term);
                score += calculateScore(docStats,term,avgDocLength,docLength);
            }
            if(score>0)
                scoreMap.put(docID,score);


            });

//        Map<String, Double> scoreMap = new HashMap<>();
//        for (String term : split) {
//            System.out.println(">>>>Term :" + term);
//            iDs.forEach((docID, docLength) -> {
//                //System.out.println(docID);
//                Response docStats = QueryHandler.getDocStats("ap_dataset", "hw1", docID, "text");
//                System.out.println("score_map_length" + scoreMap.size());
//                double score= calculateScore(docStats,term,avgDocLength,docLength);
//                if(scoreMap.containsKey(docID))
//                    scoreMap.put(docID,scoreMap.get(docID)+score);
//                else
//                    scoreMap.put(docID,score);
//            });
//        }
        return scoreMap;
    }

    private static double calculateScore(String docStats, String term, double avgDocLength, Integer docLength) {

        Pair<Integer, Integer> tfdf = QueryHandler.getTFDF(docStats, term);
        Integer tf = tfdf.getKey();
        double score = tf/(tf+ 0.5 + 1.5*(docLength/avgDocLength));
        return score;
    }

    private static void writeTofile(String path, Integer queryNo, Map<String, Double> scoreMap, int noOfDocuments) {

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            AtomicInteger atomicInteger = new AtomicInteger(1);

            File file = new File(path);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            BufferedWriter finalBw = bw;
            scoreMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                    .limit(noOfDocuments)
                    .forEach((k) -> {
                        String data = queryNo + " Q0 " + k.getKey() + " " + atomicInteger.getAndIncrement() + " " + k.getValue() + " Exp\n";
                        try {
                            finalBw.write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });


            System.out.println("Done");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
    }


//    private static <K, V extends Comparable<? super V>> Map<K, V> sortMap(Map<K, V> map) {
//        return map.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        (e1, e2) -> e1,
//                        LinkedHashMap::new
//                ));
//    }

    public static void main(String[] args) {

        Map<String, Double> items = new HashMap<>();
        items.put("A", 200.0);
        items.put("B", 20.0);
        items.put("C", 300.0);
        items.put("D", 230.0);
        items.put("E", 150.0);
        items.put("F", 60.0);

        runOkapi("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt","C:\\Users\\Sushant\\Desktop\\fs.txt", "ap_dataset", "hw1");
    }
}
