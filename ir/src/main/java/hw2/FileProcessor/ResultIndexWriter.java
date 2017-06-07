package hw2.FileProcessor;

import hw2.TermStat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sushant on 6/5/2017.
 */
public class ResultIndexWriter {

    public static void writeTofile(String filename, HashMap<String, HashMap<String, TermStat>> mapToWrite) {

        final String indexPath = "C:\\Users\\Sushant\\Desktop\\Output\\" + filename + ".txt";
        final String catalogPath = "C:\\Users\\Sushant\\Desktop\\Catalog\\" + filename + ".txt";

        BufferedWriter bw = null, catalog_bw = null;
        java.io.FileWriter fw = null, catlog_fw = null;

        try {

            AtomicInteger atomicInteger = new AtomicInteger(0);

            File file = new File(indexPath);
            File catalog_file = new File(catalogPath);
            // if file doesnt exists, then create it
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

            mapToWrite.forEach((k, v) -> {

                StringBuilder sb = new StringBuilder();

                //sb.append(k + ":");

                AtomicInteger cf_count = new AtomicInteger(0);

                v.forEach((docid, termStats) -> {
                    double tf_count = termStats.getTf();
                    sb.append(docid + "," + tf_count + "," + termStats.getPositions() + ";");
                    cf_count.addAndGet((int) tf_count);

                });

                sb.deleteCharAt(sb.length() - 1);

                sb.append("\n");

                try {
                    String temp = k + "," + v.size() + "," + cf_count + ":" + sb.toString();
                    int length = temp.getBytes().length;
                    finalBw.write(temp);
                    finalBw_catlog.write(k + ":" + atomicInteger.getAndAdd(length) + ","+length+ "\n");
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

    public static void main(String[] args) throws IOException {

        //String temp = "prepare,1,1:AP890101-0016,1.0,[103]\n";
        RandomAccessFile file = new RandomAccessFile("C:\\Users\\Sushant\\Desktop\\Output\\ap890102.txt", "r");
        file.seek(1684);
        byte[] bytes = new byte[77];
        file.read(bytes);
        //System.out.println(file.readLine());
        System.out.println(new String(bytes,"UTF-8"));
        file.close();
        //System.out.println(temp.getBytes().length);

    }
}
