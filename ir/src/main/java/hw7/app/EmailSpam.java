package hw7.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import hw7.config.PropConfig;
import hw7.ds.DocStat;
import hw7.ds.Row;
import hw7.feature.Ngram;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Sushant on 8/10/2017.
 */
public class EmailSpam {

    private static Client _Tclient = null;

    private static RestClient restClient = null;


    private static final int numofDocs = 75419;

    public static void main(String[] args) throws Exception {

        if (args.length != 1)
            throw new IllegalArgumentException("Please specify a config file");

        PropConfig prop = new PropConfig(args[0]);

        main(prop);

    }

    private static Client getTransportESClient() {
        if (_Tclient == null) {

            try {

                _Tclient = new PreBuiltTransportClient(Settings.EMPTY)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));


            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return _Tclient;
    }

    private static void main(PropConfig prop) throws Exception {

        Client transportESClient = getTransportESClient();

        generateFeatureList(prop,transportESClient);

        createTrain(prop, transportESClient);
//
        createTest(prop, transportESClient);

    }

    private static void createTest(PropConfig prop, Client transportESClient) throws IOException{
        String[] iDsfromSplit = getIDsfromSplit(transportESClient, prop.getString("test.splitQuery"));
        createData(prop, iDsfromSplit, prop.getString("test.splitQuery"),"data_test.txt");
    }

    private static void createTrain(PropConfig prop, Client transportESClient) throws Exception {

        //generateFeatureList(prop, transportESClient);
        String[] iDsfromSplit = getIDsfromSplit(transportESClient, prop.getString("train.splitQuery"));
        createData(prop, iDsfromSplit, prop.getString("train.splitQuery"),"data_train.txt");
    }

    private static void createData(PropConfig prop, String[] iDsfromSplit, String splitQuery, String file_name) throws IOException {

        Map<String, Integer> featureMap = loadFeatureMap(prop);
        Map<String, Row> data = loadWithLabel(splitQuery);
        //System.out.println(data.get("inmail.5240").getLabel());
        System.out.println("Feature map Size" + featureMap.size());

        Arrays.stream(iDsfromSplit).forEach(id -> {
            Map<Integer, Double> pair = new HashMap<>();
            try {
                Map<String, Integer> termVectorTf = getTermVectorTf(id);

                termVectorTf.forEach((term, term_fq) -> {

                    if (featureMap.containsKey(term)) {
                        int ind = featureMap.get(term);
                        pair.put(ind, new Double(term_fq));
                    }
                });

                Row row = data.get(id);
                row.setValuePair(pair);
                data.put(id, row);
            } catch (IOException e) {
                e.printStackTrace();
            }


        });


//        featureMap.entrySet().stream().filter(e -> ).forEach();
        File featureFolder = new File(prop.getString("output.folder"), "featureFolder");
        featureFolder.mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(featureFolder, file_name)))) {

            data.forEach((docID,rw)->{
                final StringBuilder sb = new StringBuilder("");
                sb.append(rw.getLabel()).append(" ");

                rw.getValuePair().entrySet().stream().sorted(Map.Entry.comparingByKey()).
                        forEachOrdered(e -> sb.append(e.getKey()).append(":").append(e.getValue()).append(" "));

                try {
                    bw.write(sb.toString());
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });



        }
    }


    private static Map<String, Row> loadWithLabel(String splitFilterQuery) {

        SearchResponse response = getTransportESClient().prepareSearch("classifier-email-1")
                .setTypes("test")
                .setSize(10000)
                .setExplain(false)
                .setFetchSource(new String[]{"label"}, null)
                .setTrackScores(false)
                .setScroll(new TimeValue(60000))
                .setQuery(QueryBuilders.wrapperQuery(splitFilterQuery)).execute()
                .actionGet();

        Map<String, Row> resultMap = new HashMap<>();


        do {
            for (SearchHit hit : response.getHits().getHits()) {
                Row row = new Row();
                String label = (String) hit.getSource().get("label");
                row.setLabel((label.equals("spam")) ? 1 : 0);
                resultMap.put(hit.getId(), row);
            }

            response = getTransportESClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
        }
        while (response.getHits().getHits().length != 0);

        return resultMap;

    }

    private static Map<String, Integer> loadFeatureMap(PropConfig prop) throws IOException {

        AtomicInteger am  = new AtomicInteger(1);
        Map<String, Integer> tempFeatureMap = new HashMap<>();
        String featureFile = prop.getString("train.featureSaved.file");
        for (String l : FileUtils.readLines(new File(featureFile))) {
            System.out.println(am.getAndIncrement());
            String[] split = l.split(":");
            Integer index = Integer.parseInt(split[0].split("=")[1]);
            String ngram = split[1].split("=")[1];

            if (ngram.split(" ").length == 1) {
                ArrayList<String> queryStemmed = getQueryStemmed(prop.getString("index.indexName"), ngram);
                if (queryStemmed.size() > 0)
                    tempFeatureMap.put(queryStemmed.get(0), index);
            } else
                tempFeatureMap.put(ngram, index);

        }
        return tempFeatureMap;
    }

    private static void generateFeatureList(PropConfig prop, Client transportESClient) throws IOException {

        System.out.println("Generating Feature List..");
        File featureFolder = new File(prop.getString("output.folder"), "featureFolder");
        featureFolder.mkdirs();

        String[] trainIDs;

        trainIDs = getIDsfromSplit(transportESClient, prop.getString("train.splitQuery"));

        System.out.println("Train IDs Captured..");

        Set<String> ngramSet = new HashSet<String>();

        if (prop.getBoolean("train.feature.addAllUnigram"))
            ngramSet.addAll(collectNgram(prop, trainIDs));

//        if(prop.getBoolean("train.feature.filterBykeywords"))
//            ngramSet = keywordFilter(prop,ngramSet);
//
//
        if (prop.getBoolean("train.feature.addFromFile"))
            ngramSet.addAll(addFeatureFromFile(prop));

        AtomicInteger am = new AtomicInteger(1);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(featureFolder, "featureList.txt")))) {
            for (String n : ngramSet) {
                if (n.length() <= 22) {
                    final StringBuilder sb = new StringBuilder("");

                    sb.append("index=").append(am.getAndIncrement()).append(":");
                    sb.append("ngram=").append(n.trim());
                    sb.append(":slop=").append(0);
                    sb.append(":inOrder=").append(true);
                    bw.write(sb.toString());
                    bw.newLine();
                }
            }
        }
        System.out.println("All n-grams are written to the file...");
    }

    private static List<String> addFeatureFromFile(PropConfig prop) throws IOException {
        List<String> ngrams = new LinkedList<>();
        String featureFile = prop.getString("train.feature.file");
        for (String l : FileUtils.readLines(new File(featureFile))) {
//            Ngram ngram = new Ngram();
//            ngram.setInOrder(true);
//            ngram.setSlop(0);
//            ngram.setNgram(l.trim());
            ngrams.add(l.trim());
        }

        System.out.println("length of n-grams from file :" + ngrams.size());
        System.out.println("External n grams loaded from File");
        return ngrams;
    }

    private static Set<String> collectNgram(PropConfig prop, String[] trainIDs) throws IOException {

        File featureFolder = new File(prop.getString("output.folder"), "meta_data");
        featureFolder.mkdirs();

        Set<String> uniquengrams = new HashSet<>();
        List<Integer> numberofNgrams = prop.getIntegers("train.feature.grams");
        double minimumDF = Double.parseDouble(prop.getString("train.feature.minDF"));
        int minimumDFFreq = (int) Math.floor(numofDocs * minimumDF);
        List<Integer> slopList = prop.getIntegers("train.feature.slop");
        System.out.println("train data size" + trainIDs.length);
        for (int nthgram : numberofNgrams) {
            for (int slp : slopList) {
                System.out.println("Collecting " + nthgram + " with slop " + slp + " minimumDF: " + minimumDFFreq);


                if (nthgram == 1 && slp == 0)
                    uniquengrams.addAll(getUnigrams(nthgram, slp, minimumDFFreq, trainIDs));

//                if(nthgram==2)
//                    allNgrams = getBigrams(nthgram,slp,minimumDFFreq);

                System.out.println("Collected " + uniquengrams.size());
//                int uniquecount = 0;
//                for (Multiset.Entry<Ngram> entry : allNgrams.entrySet()){
//                    Ngram element = entry.getElement();
//                    int count1 = entry.getCount();
//                    if(unique(uniquengrams,element,count1)){
//                        uniquengrams.add(element,count1);
//                        uniquecount++;
//                    }
//                }
            }
        }

        return uniquengrams;
    }

    private static Set<String> getUnigrams(int nthgram, int slp, int minimumDFFreq, String[] trainIDs) throws IOException {
        Set<String> mset = new HashSet<>();
        AtomicInteger am = new AtomicInteger(0);
        Arrays.stream(trainIDs).forEach(tid -> {
            try {
                Map<Integer, DocStat> termVector = getTermVector(tid);

                mset.addAll(updateMset(termVector, slp, minimumDFFreq));

                //System.out.println(tid + " : " + am.incrementAndGet() + "  size:" + mset.size());

            } catch (IOException e) {
                e.printStackTrace();
            }


        });
//        Multiset<Ngram> filteredMset = HashMultiset.create();
//        for(Multiset.Entry ent : mset.entrySet()){
//            Ngram ngram = (Ngram) ent.getElement();
//            int count = ent.getCount();
//            if(count>=minimumDFFreq)
//                filteredMset.add(ngram,count);
//
//        }

        return mset;
    }

    private static Set<String> updateMset(Map<Integer, DocStat> termVector, int slp, int minimumDFFreq) {

        List<String> collect = termVector.entrySet().stream()
                .filter(mp -> mp.getValue().getDf() >= minimumDFFreq).sorted(Map.Entry.comparingByKey())
                .map(e -> e.getValue().getTerm()).collect(Collectors.toList());
        Set<String> tempSet = new HashSet<>();
        for (String t : collect) {

            tempSet.add(t.trim());

        }
        return tempSet;
    }

    private static Map<Integer, DocStat> getTermVector(String tid) throws IOException {
        TermVectorsResponse response = getTransportESClient().prepareTermVectors("classifier-email-1", "test", tid)
                .setOffsets(false).setPositions(true).setFieldStatistics(false)
                .setTermStatistics(true)
                .setSelectedFields("body")
                .execute().actionGet();

        Map<Integer, DocStat> termPostionMap = new HashMap<>();
        Terms terms = response.getFields().terms("body");

        if (terms == null)
            return termPostionMap;

        TermsEnum iter = terms.iterator();

        for (int i = 0; i < terms.size(); i++) {

            String term = iter.next().utf8ToString();

            int term_freq = iter.postings(null).freq();

            int df = iter.docFreq();

            PostingsEnum pstenum = iter.postings(null);

            for (int k = 0; k < term_freq; k++) {
                termPostionMap.put(pstenum.nextPosition(), new DocStat(term, df));
            }

        }

        return termPostionMap;
    }

    private static Map<String, Integer> getTermVectorTf(String tid) throws IOException {
        TermVectorsResponse response = getTransportESClient().prepareTermVectors("classifier-email-1", "test", tid)
                .setOffsets(false).setPositions(true).setFieldStatistics(false)
                .setTermStatistics(true)
                .setSelectedFields("body")
                .execute().actionGet();

        Map<String, Integer> termPostionMap = new HashMap<>();
        Terms terms = response.getFields().terms("body");

        if (terms == null)
            return termPostionMap;

        TermsEnum iter = terms.iterator();

        for (int i = 0; i < terms.size(); i++) {

            String term = iter.next().utf8ToString();

            int term_freq = iter.postings(null).freq();

            int df = iter.docFreq();

            termPostionMap.put(term, term_freq);

        }

        return termPostionMap;
    }

    private static boolean unique(Multiset<Ngram> uniquengrams, Ngram element, int count1) {
        for (int slp = 0; slp < element.getSlop(); slp++) {
            Ngram pseudo = new Ngram();
            pseudo.setInOrder(element.isInOrder());
            pseudo.setNgram(element.getNgram());
            pseudo.setSlop(slp);
            if (uniquengrams.count(pseudo) == count1)
                return false;

        }

        return true;
    }

    private static String[] getIDsfromSplit(Client transportESClient, String splitFilterQuery) {

        List<String> IDs = matchQuery(splitFilterQuery);
        return IDs.toArray(new String[IDs.size()]);
    }

    private static List<String> matchQuery(String splitFilterQuery) {
        SearchResponse response = getTransportESClient().prepareSearch("classifier-email-1")
                .setTypes("test")
                .setSize(10000)
                .setExplain(false)
                .setFetchSource(false)
                .setTrackScores(false)
                .setScroll(new TimeValue(60000))
                .setQuery(QueryBuilders.wrapperQuery(splitFilterQuery)).execute()
                .actionGet();

        List<String> resultList = new ArrayList<>(response.getHits().getHits().length);


        do {
            for (SearchHit hit : response.getHits().getHits()) {
                resultList.add(hit.getId());
            }

            response = getTransportESClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
        }
        while (response.getHits().getHits().length != 0);

        return resultList;

    }

    public static ArrayList<String> getQueryStemmed(String indexName, String currentLine) {

        Response stemResponse;
        String responseEntity = "";
        ArrayList<String> terms = new ArrayList<>();
        try {


            String json = "{\n" +
                    "  \"analyzer\": \"customAnalyzer\",\n" +
                    "  \"text\": \"" + currentLine + "\"\n" +
                    "}";

            HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);

            String endPoint = "/" + indexName + "/_analyze";

            stemResponse = getrestClient().performRequest("GET", endPoint
                    ,
                    Collections.<String, String>emptyMap(), entity);

            responseEntity = EntityUtils.toString(stemResponse.getEntity());


            ObjectMapper mapper = new ObjectMapper();
            final JsonNode jsonNode = mapper.readTree(responseEntity);
            terms = (ArrayList<String>) jsonNode.get("tokens").findValuesAsText("token");


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error doing StemResponse..." + currentLine);
        } finally {
            try {
//                if(restClient!=null)
//                    restClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return terms;
        }


    }

    private static RestClient getrestClient() {

        if (restClient == null) {

            try {

                restClient = RestClient.builder(
                        new HttpHost("localhost", 9200, "http")).build();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return restClient;
    }

}
