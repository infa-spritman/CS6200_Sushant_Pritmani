package hw1.parser;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

/**
 * Created by Sushant on 5/11/2017.
 */
public class JsoupParser {

    public static Document parseDoc(String path){
        FileInputStream is = null;
        Document doc = null;
        try {
            is = new FileInputStream(path);
            doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());

        }catch(Exception e) {

            // if any I/O error occurs
            e.printStackTrace();
        } finally {

            // releases system resources associated with this stream
            if(is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return doc;
        }
    }

    public static Elements getTagFromDoc(Document d, String tag) {
        if(d!=null)
            return d.getElementsByTag(tag);

        try {
            throw new Exception("Document is not parsed properly..");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args){
//
//
//        Document doc = null;
//        InputStream is =null;
//        try {
//            is = new FileInputStream("C://Users//Sushant//Desktop//IR//data//AP89_DATA//AP_DATA//ap89_collection//ap890101");
//
//            doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
//
//            //System.out.println(doc);
//            Elements body = doc.getElementsByTag("DOC");
//            for (Element e : body) {
//                System.out.println(e.getElementsByTag("DOCNO").text());
//            }
//            //System.out.println(body);
//
//
//            // Creating Json Documents
//
////            XContentBuilder builder = jsonBuilder()
////                    .startObject()
////                    .field("user", "kimchy")
////                    .field("postDate", new Date())
////                    .field("message", "trying out Elasticsearch")
////                    .endObject();
////            String json = builder.string();
////            System.out.println(json);
//
//
//
//        }catch(Exception e) {
//
//            // if any I/O error occurs
//            e.printStackTrace();
//        } finally {
//
//            // releases system resources associated with this stream
//            if(is!=null)
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        }
//
//
//
//    }
}
