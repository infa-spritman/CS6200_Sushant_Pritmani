package hw2.start;

import hw1.json.DocumentModel;
import hw1.parser.JsoupParser;
import hw2.FileProcessor.DocIDFileWriter;
import hw2.FileProcessor.MergeIndexWriter;
import hw2.FileProcessor.ResultIndexWriter;
import hw2.POJO.DOCId;
import hw2.StanfordStemmer.CustomStemmer;
import hw2.POJO.TermStat;
import hw2.tokenizer.PTBTokenizer;
import hw2.tokenizer.TokenObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hw1.json.JsonGenerator.createDocumentObject;

/**
 * Created by Sushant on 6/5/2017.
 */
public class IndexRunner {


    public static void main(String[] args) {

        String dir = "C://Users//Sushant//Desktop//IR//data//AP89_DATA//AP_DATA//ap89_collection";
        File[] files = new File(dir).listFiles();

        Set<String> vocabularly = new LinkedHashSet<>();
        Set<String> stopList = getStopList("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\stoplist.txt");

        boolean isStop = false;
        boolean isStem = true;
        CustomStemmer cs = new CustomStemmer();

        AtomicInteger docIDGenerator = new AtomicInteger(1);
        Map<Integer, DOCId> idToDoc = new HashMap<>();


        for (File f : files) {
            if (f.isFile() && !f.getName().equalsIgnoreCase("readme")) {
                HashMap<String, HashMap<String, TermStat>> mapToWriteFile = new HashMap<>();
                Elements docs = JsoupParser.getTagFromDoc(JsoupParser.parseDoc(f.getAbsolutePath()), "DOC");
                for (Element e : docs) {

                    DocumentModel d = createDocumentObject(e);

                    LinkedList<TokenObject> tokenize = PTBTokenizer.tokenize(d.getText(), docIDGenerator.toString());

                    idToDoc.put(docIDGenerator.getAndIncrement(), new DOCId(d.getDocno(), tokenize.size()));

                    tokenize.stream()
                            .filter(t -> !stopList.contains(t.getTermId()))
                            .forEach(token -> {

                                String termId = token.getTermId();
                                if (isStem)
                                    termId = cs.stem(termId);


                                String docId = token.getDocId();
                                String position = token.getPosition();
                                vocabularly.add(termId);


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

                                        LinkedHashMap<String, TermStat> collect = stringTermStatHashMap.entrySet().stream()
                                                .sorted(Map.Entry.comparingByValue(new Comparator<TermStat>() {
                                                    @Override
                                                    public int compare(TermStat o1, TermStat o2) {
                                                        return o2.getTf().compareTo(o1.getTf());
                                                    }
                                                }))
                                                .collect(Collectors.toMap(
                                                        Map.Entry::getKey,
                                                        Map.Entry::getValue,
                                                        (e1, e2) -> e1,
                                                        LinkedHashMap::new
                                                ));
                                        mapToWriteFile.put(termId, collect);


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

//                LinkedHashMap<String, HashMap<String, TermStat>> collect = mapToWriteFile.entrySet()
//                        .stream()
//                        .sorted(Map.Entry.comparingByValue(new Comparator<HashMap<String, TermStat>>() {
//                            @Override
//                            public int compare(HashMap<String, TermStat> o1, HashMap<String, TermStat> o2) {
//                                Map.Entry<String, TermStat> nextO1 = o1.entrySet().iterator().next();
//                                Map.Entry<String, TermStat> nextO2 = o2.entrySet().iterator().next();
//
//                                return nextO2.getValue().getTf().compareTo(nextO1.getValue().getTf());
//
//                            }
//                        }))
//                        .collect(Collectors.toMap(
//                                Map.Entry::getKey,
//                                Map.Entry::getValue,
//                                (e1, e2) -> e1,
//                                LinkedHashMap::new
//                        ));


                ResultIndexWriter.writeTofile(f.getName(), mapToWriteFile);

            }

        }
        DocIDFileWriter.dumpMap(idToDoc, "C:\\Users\\Sushant\\Desktop\\Map\\DOCID.txt");
        MergeIndexWriter.merge(vocabularly);
        //System.out.println("length of al" + am.toString());
    }

    public static Set<String> getStopList(String s) {
        Set<String> stopList = new LinkedHashSet<>();
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
