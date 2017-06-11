package hw2.FileProcessor;

import hw2.POJO.DOCId;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sushant on 6/9/2017.
 */
public class DocIDFileWriter {


    public static void dumpMap(Map<Integer, DOCId> idToDoc, String path) {

        BufferedWriter bw = null;
        java.io.FileWriter fw = null;

        try {


            File fileDocID = new File(path);

            if (!fileDocID.exists()) {
                fileDocID.createNewFile();
            }

            // true = append file
            fw = new java.io.FileWriter(fileDocID.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            BufferedWriter finalBw = bw;

            idToDoc.forEach((k,v)->{
                try {
                    String docData = k + " " + v.getDocNo() + " " + v.getDocLength() + "\n";

                    finalBw.write(docData);

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
