package hw1.start;

import hw1.parser.JsoupParser;
import org.jsoup.select.Elements;

import java.io.File;

/**
 * Created by Sushant on 5/12/2017.
 */
public class Runner {

    public static void main(String[] args){

        String dir = "C://Users//Sushant//Desktop//IR//data//AP89_DATA//AP_DATA//ap89_collection//test";
        File[] files = new File(dir).listFiles();
        for(File f : files){
            if(f.isFile() && !f.getName().equalsIgnoreCase("readme")){
               Elements doc =JsoupParser.getTagFromDoc(JsoupParser.parseDoc(f.getAbsolutePath()),"DOC");

            }

        }

    }
}
