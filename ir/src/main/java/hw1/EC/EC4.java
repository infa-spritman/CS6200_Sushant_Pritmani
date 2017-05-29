package hw1.EC;

import hw1.FIleProcessor.ResultFileWriter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Math.max;

/**
 * Created by Sushant on 5/28/2017.
 */
public class EC4 {

    private static Map<String,Double> getQueryData(String filePath,int queryNo){

        Map<String,Double> queryMap = new HashMap<>();
        try (Stream<String> lines = Files.lines(Paths.get(filePath), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                if(line.startsWith(String.valueOf(queryNo))){
                    String[] lineArray = line.split(" ");
                   // System.out.println("a"+lineArray[2]+ ":" + lineArray[4]);
                    queryMap.put(lineArray[2], Double.parseDouble(lineArray[4]));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return queryMap;
    }

    private static void bordaCountTwoModels(String filePathModel1, String filePathModel2,String filePathModel3,
                                            String filePathModel4,String filePathModel5,String reportPath){

        int[] queryNoNumberArray = new int[]{64,68,71,77,80,85,87,89,91,93,94,95,97,98,99,100,54,56,57,58,59,60,61,62,63};
        for(int queryNo : queryNoNumberArray){

            Map<String, Double> queryDataModel1 = getQueryData(filePathModel1, queryNo);
            Map<String, Double> queryDataModel2 = getQueryData(filePathModel2,queryNo);
            Map<String, Double> queryDataModel3 = getQueryData(filePathModel3,queryNo);
            Map<String, Double> queryDataModel4 = getQueryData(filePathModel4,queryNo);
            Map<String, Double> queryDataModel5 = getQueryData(filePathModel5,queryNo);

            Map<String,Double> finalMap = new HashMap<>();

//            queryDataModel2.forEach((docId,value) ->{
//                if(queryDataModel1.containsKey(docId)){
//                    double new_score = max(queryDataModel1.get(docId),(5*value));
//                    queryDataModel1.put(docId, new_score);
//                }else{
//                    queryDataModel1.put(docId,5*value);
//                }
//            });
//
//            queryDataModel3.forEach((docId,value) ->{
//                if(queryDataModel1.containsKey(docId)){
//                    double new_score = max(queryDataModel1.get(docId),(3*value));
//                    queryDataModel1.put(docId, new_score);
//                }else{
//                    queryDataModel1.put(docId,3*value);
//                }
//            });

//            queryDataModel4.forEach((docId,value) ->{
//                if(queryDataModel1.containsKey(docId)){
//                    double new_score = max(queryDataModel1.get(docId) , (3*Math.pow(10,44)*Math.pow(10,value)));
//                    queryDataModel1.put(docId, new_score);
//                }else{
//                    queryDataModel1.put(docId,(3*Math.pow(10,44)*Math.pow(10,value)));
//                }
//            });
//
            queryDataModel5.forEach((docId,value) ->{
                if(queryDataModel1.containsKey(docId)){
                   finalMap.put(docId,9999.0);
                }else{
                    double new_score = max(queryDataModel1.get(docId) , (3*Math.pow(10,19)*Math.pow(10,value)));
                    queryDataModel1.put(docId, new_score);
                }
            });

            ResultFileWriter.writeTofile(reportPath, queryNo, queryDataModel1, 1000);


        }
    }

    public static void main(String[] args){

        bordaCountTwoModels("C:\\Users\\Sushant\\Desktop\\bm25.txt","C:\\Users\\Sushant\\Desktop\\okapi.txt",
                "C:\\Users\\Sushant\\Desktop\\tfIDF.txt",
                "C:\\Users\\Sushant\\Desktop\\uniLap.txt",
                "C:\\Users\\Sushant\\Desktop\\uniJM.txt","C:\\Users\\Sushant\\Desktop\\fs.txt");
//        System.out.println(25/Math.pow(10,-20));

    }
}


