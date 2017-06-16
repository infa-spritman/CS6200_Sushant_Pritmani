package hw2.Searching;

import hw2.POJO.OffsetStat;
import hw2.StanfordStemmer.CustomStemmer;
import hw2.POJO.TermStat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Sushant on 6/8/2017.
 */
public class Search {


    public static Map<String, TermStat> getStat(String word, String path) {

        String indexPath = path + "\\Final_Output\\final.txt";
        String catalogPath = path + "\\Final_Catalog\\final.txt";
        Map<String, OffsetStat> tempOffsetMap = new HashMap<>();
        Map<String, TermStat> termMap = new HashMap<>();

        try (Stream<String> lines = Files.lines(Paths.get(catalogPath), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                String[] splitLine = line.split(":");
                String[] offsetSpilit = splitLine[1].split(",");


                tempOffsetMap.put(splitLine[0], new OffsetStat(Integer.parseInt(offsetSpilit[0]),
                        Integer.parseInt(offsetSpilit[1])));

            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (tempOffsetMap.containsKey(word)) {
            OffsetStat offsetStat = tempOffsetMap.get(word);
            StringBuilder term = getTermStats(indexPath, offsetStat.getOffset(), offsetStat.getLength());
            String[] docStat = term.substring(term.indexOf(":") + 1, term.length() - 1).split(";");
            String[] split = term.substring(0, term.indexOf(":")).split(",");
            Integer df = Integer.parseInt(split[1]);
            Integer cf = Integer.parseInt(split[2]);
            for (String dt : docStat) {
                LinkedList<Integer> termPos = new LinkedList<>();
                dt = dt.replaceAll("\\[(.*?)\\]", "$1");
                String[] dt_split = dt.split(",");
                String docId = dt_split[0];

                for (int i = 2; i < dt_split.length; i++) {

                    termPos.add(Integer.parseInt(dt_split[i]));
                }
                termMap.put(docId, new TermStat(df, cf, Integer.parseInt(dt_split[1]), docId, termPos));

            }

        }


        return termMap;
    }


    private static StringBuilder getTermStats(String path, Integer offset, Integer length) {
        RandomAccessFile file = null;

        try {
            file = new RandomAccessFile(path, "r");

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


    public static void outputResultNoStemNoStop() {
        final String inputpath = "C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\in.0.50.txt";
        final String indexFolder = "C:\\Users\\Sushant\\Desktop\\IR\\Results_assignment2\\NoStopAndNoStemSorted";
        final String outputpath = "C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\noStemNoStop_in.0.50.txt";

        BufferedWriter bw = null;
        java.io.FileWriter fw = null;

        try {

            File file = new File(outputpath);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            fw = new java.io.FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            BufferedWriter finalBw = bw;


            try (Stream<String> lines = Files.lines(Paths.get(inputpath), Charset.defaultCharset())) {
                lines.forEachOrdered(line -> {
                    String word = line.trim();
                    Map<String, TermStat> wordStat = getStat(word, indexFolder);
                    Map.Entry<String, TermStat> next;
                    if (!wordStat.isEmpty()) {

                        next = wordStat.entrySet().iterator().next();

                        try {

                            finalBw.write(word + " " + next.getValue().getDf() + " " + next.getValue().getCf() + "\n");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }else{
                        try {

                            finalBw.write(word + " 0 0" + "\n");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            }

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

    public static void outputResultStemStop() {
        final String inputpath = "C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\in.0.50.txt";
        final String indexFolder = "C:\\Users\\Sushant\\Desktop\\IR\\Results_assignment2\\StopAndStemChangedRegexSorted";
        final String outputpath = "C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\stemAndStop_in.0.50.txt";

        BufferedWriter bw = null;
        java.io.FileWriter fw = null;

        try {

            File file = new File(outputpath);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            fw = new java.io.FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            BufferedWriter finalBw = bw;
            CustomStemmer cs = new CustomStemmer();

            try (Stream<String> lines = Files.lines(Paths.get(inputpath), Charset.defaultCharset())) {
                lines.forEachOrdered(line -> {
                    String word = line.trim();
                    Map<String, TermStat> wordStat = getStat(cs.stem(word), indexFolder);
                    Map.Entry<String, TermStat> next;
                    if (!wordStat.isEmpty()) {

                        next = wordStat.entrySet().iterator().next();

                        try {

                            finalBw.write(word + " " + next.getValue().getDf() + " " + next.getValue().getCf() + "\n");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }else{
                        try {

                            finalBw.write(word + " 0 0" + "\n");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            }

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

    public static void main(String[] args) {

        String path = "C:\\Users\\Sushant\\Desktop\\IR\\Results_assignment2\\StopAndStemChangedRegexSorted";

        CustomStemmer cs  = new CustomStemmer();

        Map<String, TermStat> algorithm = getStat(cs.stem("circumference"), path);
        algorithm.forEach((k,v)->{

            System.out.println(k + ":" + v.getDocId()+"," + v.getDf() +","+ v.getCf() + "," + v.getTf() +","+v.getPositions());

        });
        System.out.println("DF: " + algorithm.size());
////        //System.out.println(getTermStats(path+"\\Final_Output\\final.txt",495609357,91).toString());
//
        //outputResultNoStemNoStop();
        //outputResultStemStop();

    }
}
