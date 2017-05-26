package hw1.FIleProcessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sushant on 5/24/2017.
 */
public class ResultFileWriter {


    public static void writeTofile(String path, Integer queryNo, Map<String, Double> scoreMap, int noOfDocuments) {

        BufferedWriter bw = null;
        java.io.FileWriter fw = null;

        try {

            AtomicInteger atomicInteger = new AtomicInteger(1);

            File file = new File(path);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            fw = new java.io.FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            BufferedWriter finalBw = bw;
            scoreMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                    .limit(noOfDocuments)
                    .forEach((k) -> {
                        String data = queryNo + " Q0 " + k.getKey() + " " + atomicInteger.getAndIncrement() + " " + k.getValue() + " Exp\n";
                        try {
                            finalBw.write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });


            System.out.println("Done");

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
}
