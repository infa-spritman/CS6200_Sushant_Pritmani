package hw2.Model;

import hw1.FIleProcessor.ResultFileWriter;
import hw2.POJO.DOCId;
import hw2.POJO.TermStat;
import hw2.QueryProcessor.QueryFormatter;
import hw2.Searching.Search;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by Sushant on 6/10/2017.
 */
public class UnigramLM {

    public static void runUNIM(String queryPath, String reportPath, String indexFolder) {

        Map<Integer, LinkedList<String>> refinedQueries = QueryFormatter.getRefinedQueries(queryPath);

        AtomicInteger totalDocLength = new AtomicInteger(0);

        Map<Integer, DOCId> idToDoc = new HashMap<>();


        try (Stream<String> lines = Files.lines(Paths.get("C:\\Users\\Sushant\\Desktop\\Map\\DOCID.txt"), Charset.defaultCharset())) {
            lines.forEachOrdered(l -> {

                String[] split = l.trim().split(" ");

                int length = Integer.parseInt(split[2]);

                idToDoc.put(Integer.parseInt(split[0]), new DOCId(split[1], length));

                totalDocLength.addAndGet(length);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        double avgDocLength = (totalDocLength.doubleValue()) / idToDoc.size();

        refinedQueries.forEach((k, v) -> {
            System.out.println("Query No:" + k);
            Map<String, Double> search = search(v, avgDocLength, indexFolder, idToDoc);
            ResultFileWriter.writeTofile(reportPath, k, search, 1000);
        });

    }

    private static Map<String, Double> search(LinkedList<String> queryTerms, double avgDocLength, String indexFolder, Map<Integer, DOCId> idToDoc) {

        Map<String, Double> scoreMap = getDefaultMap(idToDoc,queryTerms.size(),198965.0);

        for (String term : queryTerms) {

            Map<String, TermStat> termStat = Search.getStat(term, indexFolder);

            termStat.forEach((k, v) -> {

                DOCId docIdObject = idToDoc.get(Integer.parseInt(k));
                String docID = docIdObject.getDocNo();
                Double docLength = Double.valueOf(docIdObject.getDocLength());
                double score = Math.log10((v.getTf()+1.0)/(docLength+198965.0)) - Math.log10(1.0/198965.0);
                if (scoreMap.containsKey(docID))
                    scoreMap.put(docID, scoreMap.get(docID) + score);


            });


        }

        return scoreMap;
    }

    private static Map<String,Double> getDefaultMap(Map<Integer, DOCId> idToDoc, int size, Double vocabSize) {
        Map<String,Double> defaultMap = new HashMap<>();
        idToDoc.forEach((k,v)->{

            defaultMap.put(v.getDocNo(),1.0*size*Math.log10(1.0/vocabSize));

        });
        return defaultMap;
    }

    public static void main(String[] args) {
        String indexpath = "C:\\Users\\Sushant\\Desktop\\IR\\Results_assignment2\\NoStopAndNoStemSorted";
        runUNIM("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\fs.txt", indexpath);


    }

}
