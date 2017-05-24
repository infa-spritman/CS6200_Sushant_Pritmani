package hw1.queryProcessor;

import hw1.elasticAPI.QueryHandler;

import java.io.*;
import java.util.*;

/**
 * Created by Sushant on 5/17/2017.
 */
public class QueryFormatter {

    public static Map<Integer, ArrayList<String>> getRefinedQueries(String queryPath,String indexName) {

        BufferedReader bis = null;
        Map<Integer, ArrayList<String>> queriesMap = new HashMap<Integer, ArrayList<String>>();
        String currentLine;
        try {
            bis = new BufferedReader(new FileReader(queryPath));
            while ((currentLine = bis.readLine()) != null && !currentLine.equals("")) {
                //System.out.println("pt"+currentLine+"pt");
                int queryNo = getQueryNo(currentLine);
                //System.out.println(queryNo);
                ArrayList<String> s = formattedString(indexName,currentLine);
                queriesMap.put(queryNo, s);
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

    private static ArrayList<String> formattedString(String indexName,String currentLine) {

        ArrayList<String> terms;

        terms = QueryHandler.getQueryStemmed(indexName,currentLine);

        return terms;

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

    private static int getQueryNo(String currentLine) {
        String[] split = currentLine.split(" ");
        return Integer.parseInt(split[0].replaceAll("\\p{P}", "").trim());
    }

    public static void main(String[] args) {
        Map<Integer, ArrayList<String>> tempHm = getRefinedQueries("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt","ap_dataset");
        for (Map.Entry m : tempHm.entrySet()) {
            System.out.println(m.getKey() + " " + m.getValue());
        }
    }
}
