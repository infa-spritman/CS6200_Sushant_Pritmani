package hw1.start;

import static hw1.model.OkapiBM25.runOkapiBM25;
import static hw1.model.OkapiTF.runOkapi;
import static hw1.model.TFIDF.runTFIDF;
import static hw1.model.UnigramJM.runUNJM;
import static hw1.model.UnigramLapalce.runULP;

/**
 * Created by Sushant on 5/25/2017.
 */
public class ModelRunner {

    public static void main(String[] args){

        runOkapi("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\okapi.txt", "ap_dataset", "hw1");
        runTFIDF("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\tfIDF.txt", "ap_dataset", "hw1");
        runOkapiBM25("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\bm25.txt", "ap_dataset", "hw1");
        runULP("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\uniLap.txt", "ap_dataset", "hw1");
        runUNJM("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\query_desc.51-100.short.txt", "C:\\Users\\Sushant\\Desktop\\uniJM.txt", "ap_dataset", "hw1");
    }
}
