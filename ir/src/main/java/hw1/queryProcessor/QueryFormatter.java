package hw1.queryProcessor;

import java.io.*;
import java.util.*;

/**
 * Created by Sushant on 5/17/2017.
 */
public class QueryFormatter {

    public static Map<Integer,String> getRefinedQueries(String queryPath) {
        
        BufferedReader bis = null;
        Map<Integer,String> queriesMap = new HashMap<Integer, String>();
        String currentLine;
        try {
            bis = new BufferedReader(new FileReader(queryPath));
            while ((currentLine = bis.readLine()) != null && !currentLine.equals("")) {
                //System.out.println("pt"+currentLine+"pt");
                int queryNo = getQueryNo(currentLine);
                //System.out.println(queryNo);
                String s = formattedString(currentLine);
                queriesMap.put(queryNo,s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return queriesMap;
    }

    private static String formattedString(String currentLine) {

        StringBuilder sb = new StringBuilder();
        Set<String> stopWords = QueryFormatter.getStopWords("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\stoplist.txt");
        String[] allText = currentLine
                .replaceAll("\\d","")
                .replaceAll("\\p{P}","").trim()
                .split(" ");

        for(String temp : allText){
            if(temp != null && !stopWords.contains(temp))
                sb.append(temp+" ");
        }

        return sb.toString().trim();
    }

    private static Set<String> getStopWords(String stopWordPath) {
        BufferedReader bis = null;
        Set<String> stopWords = new HashSet<String>();
        String currentLine;
        try {
            bis = new BufferedReader(new FileReader(stopWordPath));
            while ((currentLine = bis.readLine()) != null) {
                stopWords.add(currentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stopWords;
    }

    private static int getQueryNo(String currentLine){
        String[] split =currentLine.split(" ");
        return Integer.parseInt(split[0].replaceAll("\\p{P}","").trim());
    }

    public static void main(String[] args){
       Map<Integer,String> tempHm = getRefinedQueries("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt");
        for(Map.Entry m:tempHm.entrySet()){
            System.out.println(m.getKey()+" "+m.getValue());
        }
    }
}
