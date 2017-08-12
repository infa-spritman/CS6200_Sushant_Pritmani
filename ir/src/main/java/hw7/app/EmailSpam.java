package hw7.app;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.io.Files;
import hw2.Searching.Search;
import hw7.config.PropConfig;
import hw7.feature.Ngram;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.formula.functions.IDStarAlgorithm;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.mapper.SourceToParse;
import org.elasticsearch.index.query.QueryBuilder;
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

/**
 * Created by Sushant on 8/10/2017.
 */
public class EmailSpam {

    private static Client _Tclient = null;

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

        createTrain(prop, transportESClient);

        //createTest(prop, transportESClient);

    }

    private static void createTrain(PropConfig prop, Client transportESClient) throws Exception {

        generateFeatureList(prop, transportESClient);
    }

    private static void generateFeatureList(PropConfig prop, Client transportESClient) throws IOException {

        System.out.println("Generating Feature List..");
        File featureFolder = new File(prop.getString("output.folder"), "featureFolder");
        featureFolder.mkdirs();

        String[] trainIDs;

        trainIDs = getIDsfromSplit(transportESClient, prop.getString("train.splitQuery"));

        System.out.println("Train IDs Captured..");

        Set<Ngram> ngramSet = new HashSet<Ngram>();

//        ngramSet.addAll(collectNgram(prop,trainIDs));

//        if(prop.getBoolean("train.feature.filterBykeywords"))
//            ngramSet = keywordFilter(prop,ngramSet);
//
//
        if(prop.getBoolean("train.feature.addFromFile"))
            ngramSet.addAll(addFeatureFromFile(prop));

        AtomicInteger am = new AtomicInteger(1);
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(featureFolder,"featureList.txt")))){
            for(Ngram n : ngramSet){
                n.setIndex(am.getAndIncrement());
                bw.write(n.toString());
                bw.newLine();
            }
        }
        System.out.println("All n-grams are written to the file...");
    }

    private static List<Ngram> addFeatureFromFile(PropConfig prop) throws IOException {
        List<Ngram> ngrams = new LinkedList<>();
        String featureFile = prop.getString("train.feature.file");
        for(String l : FileUtils.readLines(new File(featureFile))){
            Ngram ngram = new Ngram();
            ngram.setInOrder(true);
            ngram.setSlop(0);
            ngram.setNgram(l.trim());
            ngrams.add(ngram);
        }

        System.out.println("External n grams loaded from File");
        return ngrams;
    }

    private static Set<Ngram> collectNgram(PropConfig prop, String[] trainIDs) {

        File featureFolder = new File(prop.getString("output.folder"),"meta_data");
        featureFolder.mkdirs();

        Multiset<Ngram> uniquengrams = ConcurrentHashMultiset.create();
        List<Integer> numberofNgrams = prop.getIntegers("train.feature.grams");
        double minimumDF = Double.parseDouble(prop.getString("train.feature.minDF"));
        int minimumDFFreq = (int) Math.floor(numofDocs*minimumDF);
        List<Integer> slopList = prop.getIntegers("train.feature.slop");
        for(int nthgram : numberofNgrams){
            for(int slp : slopList){
                System.out.println("Collecting " + nthgram + " with slop " + slp + " minimumDF: " + minimumDFFreq);


                Multiset<Ngram> allNgrams = null;
//                if(nthgram==1 && slp==0)
//                    allNgrams = getUnigrams(nthgram,slp,minimumDFFreq);

//                if(nthgram==2)
//                    allNgrams = getBigrams(nthgram,slp,minimumDFFreq);

                System.out.println("Collected " + allNgrams.elementSet().size());
                int uniquecount = 0;
                for (Multiset.Entry<Ngram> entry : allNgrams.entrySet()){
                    Ngram element = entry.getElement();
                    int count1 = entry.getCount();
                    if(unique(uniquengrams,element,count1)){
                        uniquengrams.add(element,count1);
                        uniquecount++;
                    }
                }
            }
        }

        return uniquengrams.elementSet();
    }

    private static boolean unique(Multiset<Ngram> uniquengrams, Ngram element, int count1) {
        for (int slp=0; slp<element.getSlop();slp++ ){
            Ngram pseudo = new Ngram();
            pseudo.setInOrder(element.isInOrder());
            pseudo.setNgram(element.getNgram());
            pseudo.setSlop(slp);
            if(uniquengrams.count(pseudo)==count1)
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

}
