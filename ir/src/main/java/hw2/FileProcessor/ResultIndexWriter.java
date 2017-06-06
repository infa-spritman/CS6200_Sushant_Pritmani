package hw2.FileProcessor;

import hw2.TermStat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sushant on 6/5/2017.
 */
public class ResultIndexWriter {
    public static void writeTofile(String path, HashMap<String, HashMap<String, TermStat>> mapToWrite) {


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

            mapToWrite.forEach((k, v) -> {

                StringBuilder sb = new StringBuilder();

                sb.append(k + ":");

                v.forEach((docid, termStats) -> {

                    sb.append(docid + "," + termStats.getTf() + "," + termStats.getPositions() + ";");
                });

                sb.deleteCharAt(sb.length() - 1);

                sb.append("\n");

                try {
                    finalBw.write(sb.toString());
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
