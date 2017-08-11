package hw7.app;

import hw2.Searching.Search;
import hw7.config.PropConfig;
import hw7.feature.Ngram;
import org.apache.poi.ss.formula.functions.IDStarAlgorithm;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

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

    private static void main(PropConfig prop) {

        Client transportESClient = getTransportESClient();

        createTrain(prop, transportESClient);

        //createTest(prop, transportESClient);

    }

    private static void createTrain(PropConfig prop, Client transportESClient) {

        generateFeatureList(prop, transportESClient);
    }

    private static void generateFeatureList(PropConfig prop, Client transportESClient) {

        System.out.println("Generating Feature List..");
        File featureFolder = new File(prop.getString("output.folder"), "featureFolder");
        featureFolder.mkdirs();

        String[] trainIDs;

        trainIDs = getIDsfromSplit(transportESClient, prop.getString("train.splitQuery"));

        System.out.println("Train IDs Captured..");

        Set<Ngram> ngramSet = new HashSet<Ngram>();

        ngramSet.addAll(collectNgram(prop,trainIDs));

//        if(prop.getBoolean("train.feature.filterBykeywords"))
//            ngramSet = keywordFilter(prop,ngramSet);
//
//
//        if(prop.getBoolean("train.feature.addFromFile"))
//            ngramSet.addAll(addFeatureFromFile(prop));

    }

    private static Collection<? extends Ngram> addFeatureFromFile(PropConfig prop) {
        return null;
    }

    private static Collection<? extends Ngram> collectNgram(PropConfig prop, String[] trainIDs) {
        return null;
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
