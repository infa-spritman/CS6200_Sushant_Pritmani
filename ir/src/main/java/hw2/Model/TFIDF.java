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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by Sushant on 6/9/2017.
 */
public class TFIDF {
    public static void runTFIDF(String queryPath, String reportPath, String indexFolder) {

        Map<Integer, LinkedList<String>> refinedQueries = QueryFormatter.getRefinedQueries(queryPath);
        Map<Integer, DOCId> idToDoc = new HashMap<>();
        AtomicInteger totalDocLength = new AtomicInteger(0);

        try (Stream<String> lines = Files.lines(Paths.get("C:\\Users\\Sushant\\Desktop\\Map\\DOCID.txt"), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                String[] split = line.trim().split(" ");
                int i = Integer.parseInt(split[2]);
                idToDoc.put(Integer.parseInt(split[0]), new DOCId(split[1], i));
                totalDocLength.addAndGet(i);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        double avgDocLength = (totalDocLength.doubleValue()) / idToDoc.size();
        //System.out.println(avgDocLength);
        refinedQueries.forEach((k, v) -> {
            System.out.println("Query No:" + k);
            Map<String, Double> search = search(v, avgDocLength, indexFolder,idToDoc);
            ResultFileWriter.writeTofile(reportPath, k, search, 1000);
        });

    }

    private static Map<String, Double> search(LinkedList<String> queryTerms, double avgDocLength, String indexFolder, Map<Integer, DOCId> idToDoc) {

        Map<String, Double> scoreMap = new HashMap<>();

        for (String term : queryTerms) {

            Map<String, TermStat> termStat = Search.getStat(term, indexFolder);

            termStat.forEach((k, v) -> {

                //System.out.println(k + ":" + v.getDocId() + "," + v.getDf() + "," + v.getCf() + "," + v.getTf() + "," + v.getPositions());
                DOCId docIdObject = idToDoc.get(Integer.parseInt(k));
                //System.out.println(k);
                //System.out.println(v.getDocId());
                String docID = docIdObject.getDocNo();
                Double docLength  = Double.valueOf(docIdObject.getDocLength());
                double score =  ((1.0*v.getTf())/(v.getTf()+ 0.5 + 1.5*(docLength/avgDocLength))) * Math.log10((idToDoc.size()*1.0)/v.getDf());
                if (scoreMap.containsKey(docID))
                    scoreMap.put(docID, scoreMap.get(docID) + score);
                else
                    scoreMap.put(docID, score);

            });


        }

        return scoreMap;
    }

    public static void main(String[] args) {
        String indexpath = "C:\\Users\\Sushant\\Desktop\\IR\\Results_assignment2\\NoStopAndNoStemSorted";
        runTFIDF("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\fs.txt", indexpath);


    }

}
