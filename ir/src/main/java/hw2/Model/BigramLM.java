package hw2.Model;

import hw1.FIleProcessor.ResultFileWriter;
import hw2.POJO.DOCId;
import hw2.POJO.TermStat;
import hw2.QueryProcessor.QueryFormatter;
import hw2.Searching.Search;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Sushant on 6/12/2017.
 */
public class BigramLM {

    public static void runBLM(String queryPath, String reportPath, String indexFolder) {

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

        Map<String, Double> scoreMap = getDefaultMap(idToDoc, queryTerms.size(), 157266.0);

        for (int i = 0; i < queryTerms.size() - 1; i++) {

            Map<String, LinkedList<TermStat>> docList = getDocList(new LinkedList<>(Arrays.asList(queryTerms.get(i), queryTerms.get(i + 1))), indexFolder);

            docList.entrySet().stream().filter(map -> map.getValue().size() == 2)
                    .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()))
                    .forEach((docID, termStatList) -> {

                        Double count_Wn = countBigrams(termStatList);

                        DOCId docIdObject = idToDoc.get(Integer.parseInt(docID));
                        String docIDString = docIdObject.getDocNo();
                        //Double docLength = Double.valueOf(docIdObject.getDocLength());

                        double score = Math.log10((count_Wn + 1.0) / (termStatList.get(0).getTf() + 157266.0)) - Math.log10(1.0 / 157266.0);
                        if (scoreMap.containsKey(docIDString))
                            scoreMap.put(docIDString, scoreMap.get(docIDString) + score);


                    });

        }

        return scoreMap;
    }

    private static Double countBigrams(LinkedList<TermStat> termStatList) {
        DoubleAdder da = new DoubleAdder();
        LinkedList<Integer> term1_positions = termStatList.get(0).getPositions();
        LinkedList<Integer> term2_positions = termStatList.get(1).getPositions();

        term1_positions.forEach(t1-> {
            term2_positions.forEach(t2->{
               da.add(1.0/Math.abs(t2 - t1));


            });

        });

        return da.doubleValue();
    }

    private static Map<String, LinkedList<TermStat>> getDocList(LinkedList<String> bigramTerms, String indexFolder) {

        Map<String, LinkedList<TermStat>> docPostingList = new HashMap<>();
        for (String term : bigramTerms) {

            Map<String, TermStat> termStat = Search.getStat(term, indexFolder);

            termStat.forEach((k, v) -> {

                if (docPostingList.containsKey(k)) {
                    LinkedList<TermStat> tsTemp = docPostingList.get(k);
                    tsTemp.add(v);
                    docPostingList.put(k, tsTemp);

                } else {
                    LinkedList<TermStat> tsTemp = new LinkedList<>();
                    tsTemp.add(v);
                    docPostingList.put(k, tsTemp);
                }
            });
        }

        return docPostingList;
    }

    private static Map<String, Double> getDefaultMap(Map<Integer, DOCId> idToDoc, int size, Double vocabSize) {
        Map<String, Double> defaultMap = new HashMap<>();
        idToDoc.forEach((k, v) -> {

            defaultMap.put(v.getDocNo(), 1.0 * (size - 1.0) * Math.log10(1.0 / vocabSize));

        });
        return defaultMap;
    }

    public static void main(String[] args) {
        String indexpath = "C:\\Users\\Sushant\\Desktop\\IR\\Results_assignment2\\StopAndStemDocId";
        runBLM("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\fs.txt", indexpath);


    }
}
