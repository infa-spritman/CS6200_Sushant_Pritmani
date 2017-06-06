package hw2.start;

import hw1.json.DocumentModel;
import hw1.parser.JsoupParser;
import hw2.FileProcessor.ResultIndexWriter;
import hw2.TermStat;
import hw2.tokenizer.PTBTokenizer;
import hw2.tokenizer.TokenObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import static hw1.json.JsonGenerator.createDocumentObject;

/**
 * Created by Sushant on 6/5/2017.
 */
public class IndexRunner {


    public static void main(String[] args) {

        String dir = "C://Users//Sushant//Desktop//IR//data//AP89_DATA//AP_DATA//ap89_collection";
        File[] files = new File(dir).listFiles();
        for (File f : files) {
            if (f.isFile() && !f.getName().equalsIgnoreCase("readme")) {
                HashMap<String, HashMap<String, TermStat>> mapToWriteFile = new HashMap<>();
                Elements docs = JsoupParser.getTagFromDoc(JsoupParser.parseDoc(f.getAbsolutePath()), "DOC");
                for (Element e : docs) {

                    DocumentModel d = createDocumentObject(e);

                    LinkedList<TokenObject> tokenize = PTBTokenizer.tokenize(d.getText(), d.getDocno());

                    tokenize.stream().forEach(token -> {

                        String termId = token.getTermId();
                        String docId = token.getDocId();
                        String position = token.getPosition();

                        if (mapToWriteFile.containsKey(termId)) {

                            HashMap<String, TermStat> stringTermStatHashMap = mapToWriteFile.get(termId);

                            if (stringTermStatHashMap.containsKey(docId)) {

                                TermStat termStat = stringTermStatHashMap.get(docId);

                                LinkedList<Integer> positions = termStat.getPositions();

                                positions.add(Integer.parseInt(position));

                                stringTermStatHashMap.put(docId, new TermStat(termStat.getDf(),
                                        termStat.getCf() + 1.0,
                                        termStat.getTf() + 1.0,
                                        docId,
                                        positions));

                                mapToWriteFile.put(termId, stringTermStatHashMap);

                            } else {
                                LinkedList<Integer> positions = new LinkedList<>();

                                positions.add(Integer.parseInt(position));

                                stringTermStatHashMap.put(docId, new TermStat(1.0, 1.0, 1.0, docId, positions));

                                mapToWriteFile.put(termId, stringTermStatHashMap);

                            }

                        } else {

                            LinkedList<Integer> positions = new LinkedList<>();

                            positions.add(Integer.parseInt(position));

                            HashMap<String, TermStat> stringTermStatHashMap = new HashMap<>();

                            stringTermStatHashMap.put(docId, new TermStat(1.0, 1.0, 1.0, docId, positions));

                            mapToWriteFile.put(termId, stringTermStatHashMap);
                        }
                    });


                }

                ResultIndexWriter.writeTofile( "C:\\Users\\Sushant\\Desktop\\Output\\"+f.getName()+".txt",mapToWriteFile);

            }

        }

    }

}
