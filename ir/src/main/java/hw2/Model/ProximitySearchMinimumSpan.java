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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by Sushant on 6/11/2017.
 */
public class ProximitySearchMinimumSpan {


    public static void runPMSMS(String queryPath, String reportPath, String indexFolder) {

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
            Map<String, Double> search = search(v, avgDocLength, indexFolder, idToDoc);
            ResultFileWriter.writeTofile(reportPath, k, search, 1000);
        });

    }


    private static Map<String, Double> search(LinkedList<String> queryTerms, double avgDocLength, String indexFolder, Map<Integer, DOCId> idToDoc) {

        Map<String, Double> scoreMap = new HashMap<>();
        Map<String, LinkedList<TermStat>> docPostingList = new HashMap<>();
        for (String term : queryTerms) {

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

        docPostingList.forEach((docID,termStatList)->{

            Integer minimumSpan = rangeofWindow(termStatList);

            DOCId docIdObject = idToDoc.get(Integer.parseInt(docID));
            String docIDString = docIdObject.getDocNo();
            Double docLength  = Double.valueOf(docIdObject.getDocLength());

            double score =  ((1500.0- minimumSpan)*(termStatList.size()))/(docLength+157266.0);

            if (scoreMap.containsKey(docID))
                    scoreMap.put(docIDString, scoreMap.get(docID) + score);
            else
                    scoreMap.put(docIDString, score);


        });


        return scoreMap;
    }

    private static Integer rangeofWindow(LinkedList<TermStat> termStatList) {

        PriorityQueue<Integer> maxheap = new PriorityQueue<>(termStatList.size(), Collections.reverseOrder());
        PriorityQueue<Integer> minheap = new PriorityQueue<>(termStatList.size());


        Integer range = Integer.MAX_VALUE;


        int [] pointersArray = new int[termStatList.size()];
        Arrays.fill(pointersArray,0);

//        termStatList.forEach(ts->{
//
//            Integer firstPosition = ts.getPositions().get(0);
//
//            maxheap.offer(firstPosition);
//            minheap.offer(firstPosition);
//
//        });


        //range = maxheap.peek() - minheap.peek();


        for(int i =0;i<termStatList.size();i++){
            Integer firstPosition = termStatList.get(i).getPositions().get(pointersArray[i]);

            maxheap.offer(firstPosition);
            minheap.offer(firstPosition);

        }
        AtomicInteger am  = new AtomicInteger(1);

        while(!checkForLastIndex(termStatList,pointersArray)){

            

            Integer diff = maxheap.peek() - minheap.peek();
            if(diff<range)
                range = diff;


            Integer minHeapHead = kthSmallest(minheap,am.get());
            //maxheap.remove(minHeapHead);

            for(int i =0;i<termStatList.size();i++){
                Integer firstPosition = termStatList.get(i).getPositions().get(pointersArray[i]);
                if(minHeapHead.equals(firstPosition)){
                    if((termStatList.get(i).getTf()-1)==pointersArray[i]){
                            am.incrementAndGet();

                    }else{
                        minheap.remove(minHeapHead);
                        maxheap.remove(minHeapHead);
                        pointersArray[i]++;
                        Integer newInteger = termStatList.get(i).getPositions().get(pointersArray[i]);
                        maxheap.offer(newInteger);
                        minheap.offer(newInteger);

                    }

                }

            }

        }

        return range;

    }

    private static boolean checkForLastIndex(LinkedList<TermStat> termStatList, int[] pointersArray) {
        boolean toBereturned = true;

        for(int i =0;i<termStatList.size();i++){
            boolean b = (termStatList.get(i).getTf() - 1) == pointersArray[i];
            toBereturned = toBereturned && b;

        }

        return toBereturned;
    }


    public static Integer kthSmallest(PriorityQueue<Integer> queue, int k) throws IllegalArgumentException {
        if (k < 1 || k > queue.size()) {
            throw new IllegalArgumentException("k:" + k + " to small or to large for queueSize:" + queue.size());
        }
        Queue<Integer> buffer = new PriorityQueue<Integer>();
        buffer.addAll(queue);
        int kth = 0;
        final int size = buffer.size();
        for (int i = 0; i < size; i++) {
            if(i < k) {
                kth = buffer.peek();
            }
            buffer.poll();
        }
        return kth;
    }

    public static void main(String[] args) {


//        LinkedList<TermStat> termStats = new LinkedList<>();
//        termStats.add(new TermStat(0,0,4,"A", new LinkedList<>(Arrays.asList(0, 5, 10,15))));
//        termStats.add(new TermStat(0,0,4,"A", new LinkedList<>(Arrays.asList(1, 3,6,9))));
//        termStats.add(new TermStat(0,0,4,"A", new LinkedList<>(Arrays.asList(4,8,16,21))));
//
//        System.out.println(rangeofWindow(termStats));


        String indexpath = "C:\\Users\\Sushant\\Desktop\\IR\\Results_assignment2\\StopAndStemChangedRegex";
        runPMSMS("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\fs.txt", indexpath);


    }
}
