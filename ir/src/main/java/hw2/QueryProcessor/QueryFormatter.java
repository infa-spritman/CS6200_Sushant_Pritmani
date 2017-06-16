package hw2.QueryProcessor;

import hw2.StanfordStemmer.CustomStemmer;
import hw2.tokenizer.PTBTokenizer;
import hw2.tokenizer.TokenObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static hw2.start.IndexRunner.getStopList;

/**
 * Created by Sushant on 6/9/2017.
 */
public class QueryFormatter {

    public static Map<Integer, LinkedList<String>> getRefinedQueries(String queryPath) {

        BufferedReader bis = null;
        Map<Integer, LinkedList<String>> queriesMap = new HashMap<Integer, LinkedList<String>>();
        Set<String> stopList = getStopList("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\stoplist.txt");
        CustomStemmer cs = new CustomStemmer();
        String currentLine;
        try {
            bis = new BufferedReader(new FileReader(queryPath));
            while ((currentLine = bis.readLine()) != null && !currentLine.equals("")) {
                //System.out.println("pt"+currentLine+"pt");
                LinkedList<TokenObject> tokenize = PTBTokenizer.tokenize(currentLine.substring(currentLine.indexOf(".") +1), "QUERY");
                LinkedList<String> queryTerms = new LinkedList<>();

                tokenize.stream().filter(t -> !stopList.contains(t.getTermId())).forEach(token -> {

                    queryTerms.add(token.getTermId());
                });

                int queryNo = getQueryNo(currentLine);

                queriesMap.put(queryNo,queryTerms);
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

    private static int getQueryNo(String currentLine) {
        String[] split = currentLine.split(" ");
        return Integer.parseInt(split[0].replaceAll("\\p{P}", "").trim());
    }

    public static void main(String[] args) {
        Map<Integer, LinkedList<String>> tempHm = getRefinedQueries("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt");
        for (Map.Entry m : tempHm.entrySet()) {
            System.out.println(m.getKey() + " " + m.getValue());
        }
    }
}
