package hw2.FileProcessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sushant on 6/6/2017.
 */
public class MergeIndexWriter {


    public static void merge(Set<String> vocabularly) {

        String filename = "final";
        final String indexPath = "C:\\Users\\Sushant\\Desktop\\Output\\" + filename + ".txt";
        final String catalogPath = "C:\\Users\\Sushant\\Desktop\\Catalog\\" + filename + ".txt";

        BufferedWriter bw = null, catalog_bw = null;
        java.io.FileWriter fw = null, catlog_fw = null;
        Map<String,Map<String,Integer>> catalogMap = new LinkedHashMap<>();


        try {

            AtomicInteger atomicInteger = new AtomicInteger(1);

            File file = new File(indexPath);
            File catalog_file = new File(catalogPath);

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

            System.out.println("Done Final Index");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (catalog_bw != null)
                    catalog_bw.close();

                if (fw != null)
                    fw.close();

                if (catlog_fw != null)
                    catlog_fw.close();


            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }

    }
}
