package hw2.FileProcessor;

import hw2.POJO.OffsetStat;
import hw2.POJO.TermStat;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by Sushant on 6/6/2017.
 */
public class MergeIndexWriter {


    public static void merge(Set<String> vocabularly) {

        String filename = "final";
        final String finalIndexPath = "C:\\Users\\Sushant\\Desktop\\Final_Output\\" + filename + ".txt";
        final String finalCatalogPath = "C:\\Users\\Sushant\\Desktop\\Final_Catalog\\" + filename + ".txt";
        final String indexDir = "C:\\Users\\Sushant\\Desktop\\Output";
        final String catlogDir = "C:\\Users\\Sushant\\Desktop\\Catalog";


        BufferedWriter bw = null, catalog_bw = null;
        java.io.FileWriter fw = null, catlog_fw = null;
        Map<String, Map<String, OffsetStat>> catalogMap = new LinkedHashMap<>();


        try {

            AtomicInteger atomicInteger = new AtomicInteger(0);

            File file = new File(finalIndexPath);
            File catalog_file = new File(finalCatalogPath);

            if (!file.exists()) {
                file.createNewFile();
            }

            if (!catalog_file.exists()) {
                catalog_file.createNewFile();
            }

            // true = append file
            fw = new java.io.FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            BufferedWriter finalBw = bw;

            //catalog
            catlog_fw = new java.io.FileWriter(catalog_file.getAbsoluteFile(), true);
            catalog_bw = new BufferedWriter(catlog_fw);
            BufferedWriter finalBw_catlog = catalog_bw;


            try (Stream<Path> paths = Files.walk(Paths.get(catlogDir))) {
                paths.forEach((p) -> {
                    File file1 = p.toFile();
                    if (!file1.isDirectory()) {
                        String fname = file1.getName();
                        fname = fname.substring(0, fname.lastIndexOf("."));
                        Map<String, OffsetStat> tempOffsetMap = new HashMap<>();

                        try (Stream<String> lines = Files.lines(p, Charset.defaultCharset())) {
                            lines.forEachOrdered(line -> {
                                String[] splitLine = line.split(":");
                                String[] offsetSpilit = splitLine[1].split(",");

                                tempOffsetMap.put(splitLine[0], new OffsetStat(Integer.parseInt(offsetSpilit[0]),
                                        Integer.parseInt(offsetSpilit[1])));

                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        catalogMap.put(fname, tempOffsetMap);
                        //System.out.println(fname + ":" + tempOffsetMap.size());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

//            mapToWrite.forEach((k, v) -> {
//
//                StringBuilder sb = new StringBuilder();
//
//                //sb.append(k + ":");
//
//                AtomicInteger cf_count = new AtomicInteger(0);
//
//                v.forEach((docid, termStats) -> {
//                    double tf_count = termStats.getTf();
//                    sb.append(docid + "," + tf_count + "," + termStats.getPositions() + ";");
//                    cf_count.addAndGet((int) tf_count);
//
//                });
//
//                sb.deleteCharAt(sb.length() - 1);
//
//                sb.append("\n");
//
//                try {
//                    finalBw.write(k + "," + v.size() + "," + cf_count + ":" + sb.toString());
//                    finalBw_catlog.write(k + ":" + atomicInteger.getAndIncrement() + "\n");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            });


            //System.out.println("Size" + catalogMap.size());

            vocabularly.stream().forEach(word -> {
                StringBuilder termStatToWrite = new StringBuilder();
                PriorityQueue<TermStat> maxTFHeaf = new PriorityQueue<>();
                AtomicInteger df = new AtomicInteger(0);
                AtomicInteger cf = new AtomicInteger(0);

                catalogMap.forEach((fl_name, offsetMap) -> {
                    if (offsetMap.containsKey(word)) {
                        OffsetStat offsetStat = offsetMap.get(word);
                        StringBuilder term = getTerm(fl_name, offsetStat.getOffset(), offsetStat.getLength());
                       // termStatToWrite.append(term.substring(term.indexOf(":") + 1, term.length() - 1)).append(";");
                        String posting = term.substring(term.indexOf(":") + 1, term.length() - 1);
                        String[] postingArray = posting.split(";");
                        Arrays.stream(postingArray).forEach(s->{

                            String[] termStat = s.split(",");
                            LinkedList<Integer> tempPositions = new LinkedList<>();
                             for(int i=2;i<termStat.length;i++){

                                tempPositions.add(Integer.parseInt(termStat[i]));
                             }

                            maxTFHeaf.offer(new TermStat(0,0,Integer.parseInt(termStat[1]),termStat[0],tempPositions));


                        });


                        String[] split = term.substring(0, term.indexOf(":")).split(",");
                        df.addAndGet(Integer.parseInt(split[1]));
                        cf.addAndGet(Integer.parseInt(split[2]));
                    }

                });

                while(!maxTFHeaf.isEmpty()){

                    TermStat poll = maxTFHeaf.poll();
                    termStatToWrite.append(poll.getDocId() + "," + poll.getTf() + "," + convertToString(poll.getPositions().toArray()) + ";");

                }
                termStatToWrite.deleteCharAt(termStatToWrite.length() - 1);

                termStatToWrite.append("\n");
                try {
                    //StringBuilder tempStringToWrite = new StringBuilder().append(word)
                    String tempString = word + "," + df + "," + cf + ":" + termStatToWrite.toString();
                    Integer length = tempString.getBytes().length;
                    finalBw.write(tempString);
                    finalBw_catlog.write(word + ":" + atomicInteger.getAndAdd(length) + ","+length+ "\n");
                    //System.out.println(word + "merged");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (catalog_bw != null)
                    catalog_bw.close();

                if (bw != null) {
                    bw.close();
                }

                if (fw != null)
                    fw.close();

                if (catlog_fw != null)
                    catlog_fw.close();


            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }

    }

    private static StringBuilder getTerm(String fl_name, Integer offset, Integer length) {
        RandomAccessFile file = null;

        try {
            file = new RandomAccessFile("C:\\Users\\Sushant\\Desktop\\Output\\" + fl_name + ".txt", "r");

            file.seek(offset);

            byte[] bytes = new byte[length];

            file.read(bytes);

            //System.out.println(new String(bytes, "UTF-8"));

            file.close();

            return new StringBuilder(Charset.forName("UTF-8").decode(ByteBuffer.wrap(bytes)));


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String convertToString(Object[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append("");
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append("").toString();
            b.append(",");
        }
    }

//    public static void main(String[] args) {
//
//
//        merge(new HashSet<>());
//
//    }
}
