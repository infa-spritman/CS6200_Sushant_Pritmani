package hw2.start;

import hw1.json.DocumentModel;
import hw1.parser.JsoupParser;
import hw2.FileProcessor.MergeIndexWriter;
import hw2.FileProcessor.ResultIndexWriter;
import hw2.StanfordStemmer.CustomStemmer;
import hw2.TermStat;
import hw2.tokenizer.PTBTokenizer;
import hw2.tokenizer.TokenObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static hw1.json.JsonGenerator.createDocumentObject;

/**
 * Created by Sushant on 6/5/2017.
 */
public class IndexRunner {


    public static void main(String[] args) {

        String dir = "C://Users//Sushant//Desktop//IR//data//AP89_DATA//AP_DATA//ap89_collection";
        File[] files = new File(dir).listFiles();
        AtomicInteger am  = new AtomicInteger(0);
        Set<String> vocabularly = new LinkedHashSet<>();
        Set<String> stopList  = getStopList("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\stoplist.txt");
        boolean isStop = false;
        boolean isStem = true;
        CustomStemmer cs = new CustomStemmer();
        for (File f : files) {
            if (f.isFile() && !f.getName().equalsIgnoreCase("readme")) {
                HashMap<String, HashMap<String, TermStat>> mapToWriteFile = new HashMap<>();
                Elements docs = JsoupParser.getTagFromDoc(JsoupParser.parseDoc(f.getAbsolutePath()), "DOC");
                for (Element e : docs) {

                    DocumentModel d = createDocumentObject(e);

                    LinkedList<TokenObject> tokenize = PTBTokenizer.tokenize(d.getText(), d.getDocno());

                    tokenize.stream().filter(t-> !stopList.contains(t.getTermId())).forEach(token -> {

                        String termId = token.getTermId();
                        if(isStem)
                               termId = cs.stem(termId);

                        String docId = token.getDocId();
                        String position = token.getPosition();
                        vocabularly.add(termId);

                        if(termId.equalsIgnoreCase("algorithm") || termId.equalsIgnoreCase("algorithms"))
                            am.getAndIncrement();


                        if (mapToWriteFile.containsKey(termId)) {

                            HashMap<String, TermStat> stringTermStatHashMap = mapToWriteFile.get(termId);

                            if (stringTermStatHashMap.containsKey(docId)) {

                                TermStat termStat = stringTermStatHashMap.get(docId);

                                LinkedList<Integer> positions = termStat.getPositions();

                                positions.add(Integer.parseInt(position));

                                stringTermStatHashMap.put(docId, new TermStat(termStat.getDf(),
                                        termStat.getCf() + 1,
                                        termStat.getTf() + 1,
                                        docId,
                                        positions));

                                mapToWriteFile.put(termId, stringTermStatHashMap);

                            } else {
                                LinkedList<Integer> positions = new LinkedList<>();

                                positions.add(Integer.parseInt(position));

                                stringTermStatHashMap.put(docId, new TermStat(1, 1, 1, docId, positions));

                                mapToWriteFile.put(termId, stringTermStatHashMap);

                            }

                        } else {

                            LinkedList<Integer> positions = new LinkedList<>();

                            positions.add(Integer.parseInt(position));

                            HashMap<String, TermStat> stringTermStatHashMap = new HashMap<>();

                            stringTermStatHashMap.put(docId, new TermStat(1, 1, 1, docId, positions));

                            mapToWriteFile.put(termId, stringTermStatHashMap);
                        }
                    });


                }

                ResultIndexWriter.writeTofile( f.getName() ,mapToWriteFile);

            }

        }

        MergeIndexWriter.merge(vocabularly);
        //System.out.println("length of al" + am.toString());
    }

    private static Set<String> getStopList(String s) {
        Set<String > stopList  = new LinkedHashSet<>();
        try (Stream<String> lines = Files.lines(Paths.get(s), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
               // System.out.println(line.trim());
                stopList.add(line.trim());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopList;
    }

}
