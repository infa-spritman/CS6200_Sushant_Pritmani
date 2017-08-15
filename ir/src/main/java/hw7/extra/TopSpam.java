package hw7.extra;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by Sushant on 8/14/2017.
 */
public class TopSpam {


    public static void main(String[] args){

        System.out.println(getAccuracy("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\PartA\\prob_test_parta.txt",
                                       "C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\PartA\\data_test.txt"));

        //System.out.println(getAccuracy("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\PartB\\prob_test_partB.txt",
         //       "C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\PartB\\data_test.txt"));
    }

    private static Double getAccuracy(String prob, String Input) {

        LinkedHashMap<Integer,Double> probMap = new LinkedHashMap();
        LinkedHashMap<Integer,Double> inputMap = new LinkedHashMap();
        LinkedHashMap<Integer,Double> sortedprobMap = new LinkedHashMap();


        AtomicInteger am = new AtomicInteger(1);
        try (Stream<String> lines = Files.lines(Paths.get(prob), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                String[] split = line.split(" ");
                probMap.put(am.getAndIncrement(), Double.parseDouble(split[1]));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        am.set(1);
        try (Stream<String> lines = Files.lines(Paths.get(Input), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                String[] split = line.split(" ");
                inputMap.put(am.getAndIncrement(), Double.parseDouble(split[0]));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        probMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .limit(50)
                .forEach((k) -> {
                    sortedprobMap.put(k.getKey(),k.getValue());
                });


        am.set(0);

        sortedprobMap.forEach((k,v) ->{

            System.out.println("Line number :" + k + " Value: " + v);
            if(inputMap.get(k)==1.0)
                am.getAndIncrement();

        });

        return new Double(am.get())/50.0;

    }
}
