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
 * Created by Sushant on 6/16/2017.
 */
public class TFIDfAndHead {

    public static void runTFIDF(String queryPath, String reportPath, String indexFolder) {

        Map<Integer, LinkedList<String>> refinedQueries = QueryFormatter.getRefinedQueries(queryPath);
        Map<Integer, DOCId> idToDoc = new HashMap<>();
        Map<Integer, DOCId> idToDocHead = new HashMap<>();

        AtomicInteger totalDocLength = new AtomicInteger(0);
        AtomicInteger totalHeadLength = new AtomicInteger(0);

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

        try (Stream<String> lines = Files.lines(Paths.get("C:\\Users\\Sushant\\Desktop\\IR\\Results_assignment2\\HeadStopAndStem\\Map\\DOCID.txt"), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                String[] split = line.trim().split(" ");
                int i = Integer.parseInt(split[2]);
                idToDocHead.put(Integer.parseInt(split[0]), new DOCId(split[1], i));
                totalHeadLength.addAndGet(i);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        double avgDocLength = (totalDocLength.doubleValue()) / idToDoc.size();
        double avgHeadLength = (totalHeadLength.doubleValue())/idToDocHead.size();
        //System.out.println(avgDocLength);
        refinedQueries.forEach((k, v) -> {
            System.out.println("Query No:" + k);
            Map<String, Double> search = search(v, avgDocLength, indexFolder,idToDoc,idToDocHead,avgHeadLength);
            ResultFileWriter.writeTofile(reportPath, k, search, 1000);
        });

    }



    private static Map<String, Double> search(LinkedList<String> queryTerms, double avgDocLength, String indexFolder, Map<Integer, DOCId> idToDoc, Map<Integer, DOCId> idToDocHead, double avgHeadLength) {

        Map<String, Double> scoreMap = new HashMap<>();

        String headFolder = "C:\\Users\\Sushant\\Desktop\\IR\\Results_assignment2\\HeadStopAndStem";

        for (String term : queryTerms) {

            Map<String, TermStat> termStat = Search.getStat(term, indexFolder);

            Map<String,TermStat>  docStat  = Search.getStat(term, headFolder);


            termStat.forEach((k, v) -> {

                //System.out.println(k + ":" + v.getDocId() + "," + v.getDf() + "," + v.getCf() + "," + v.getTf() + "," + v.getPositions());
                DOCId docIdObject = idToDoc.get(Integer.parseInt(k));
                //System.out.println(k);
                //System.out.println(v.getDocId());
                String docID = docIdObject.getDocNo();
                Double docLength  = Double.valueOf(docIdObject.getDocLength());
                Double tf = v.getTf() + 1.5*getHeadCount(docStat,k);

                double score =  ((1.0*tf)/(tf + 0.5 + 1.5*(docLength/avgDocLength)))* Math.log10((idToDoc.size()*1.0)/v.getDf());
                if (scoreMap.containsKey(docID))
                    scoreMap.put(docID, scoreMap.get(docID) + score);
                else
                    scoreMap.put(docID, score);

            });


        }

        return scoreMap;
    }

    public static Integer getHeadCount( Map<String,TermStat>  docStat, String docId){

        if(docStat.containsKey(docId))
            return docStat.get(docId).getTf();
        else
            return 0;

    }
    public static void main(String[] args) {
        String indexpath = "C:\\Users\\Sushant\\Desktop\\IR\\Results_assignment2\\StopAndStemChangedRegexSorted";
        runTFIDF("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\fs.txt", indexpath);


    }
}
