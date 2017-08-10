package hw3.Crawler;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import hw3.POJO.QueueElement;
import hw3.json.JsonGenerator;
import hw3.URLTools.Urlnorm;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.script.Script;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static hw2.start.IndexRunner.getStopList;

/**
 * Created by Sushant on 6/22/2017.
 */
public class CrawlWebs {

    static Map<String, BaseRobotRules> robotsTxtRules = new HashMap<String, BaseRobotRules>();
    private static HttpClient httpclient = HttpClientBuilder.create().build();
    static Set<String> visited = new HashSet<>();
    private static final String cssLinkSelector = "a[href]:not([href~=(?i).*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
            "|rm|smil|wmv|swf|wma|zip|rar|gz|csv|xls|ppt|doc|docx|exe|dmg|midi|mid|qt|txt|ram|json))$)" +
            ":not([href~=(?i)^#])";

    static long delay = 700;
    static BufferedWriter bw = null;
    static java.io.FileWriter fw = null;

    private static Client _Tclient = null;


//    static Queue<String> linkQueue;
//    static LinkedList<String> secondaryQueue = new LinkedList<>();

    static Map<String, QueueElement> tempQueueMap = new HashMap<>();
    static PriorityQueue<QueueElement> linksQueue = new PriorityQueue<>();
    static Map<String, Long> politenessMap = new HashMap<>();
    static Set<String> relavantList = getStopList("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\wordsList.txt");


    public static void crawl(ArrayList<String> seedURls, String dir) {

        Set<String> stopList = getStopList("C:\\Users\\Sushant\\Documents\\GitHub\\CS6200_Sushant_Pritmani\\ir\\src\\main\\resources\\stoplist.txt");

        AtomicInteger noOFKey = new AtomicInteger(2);
        File file = new File(dir + "data.txt");
        // if file doesnt exists, then create it
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fw = new java.io.FileWriter(file.getAbsoluteFile(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bw = new BufferedWriter(fw);

        AtomicInteger noOfDocuments = new AtomicInteger(seedURls.size());

        seedURls.stream().forEach(surl -> {

            //boolean urlAllowed = checkRobot(surl);
            String normalisedURL = Urlnorm.norm(surl);

            if (!visited.contains(normalisedURL)) {


                if (checkRobot(surl)) {


                    try {
                        politenessMap.put(new URL(surl).getHost().toLowerCase().replaceFirst("www.", ""), System.currentTimeMillis());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    try {


                        String fetchedUrl = getFinalRedirectedUrl(Urlnorm.decode(surl));
                        //System.out.println("FetchedURL is:" + Urlnorm.decode(fetchedUrl));
                        String normFetch = Urlnorm.norm(fetchedUrl);
                        if (!visited.contains(normFetch)) {

                            Connection.Response response = Jsoup.connect(Urlnorm.decode(fetchedUrl))
                                    //.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2 Firefox/25.0")
                                    //.referrer("http://www.google.com")
                                    .timeout(7000)
                                    .followRedirects(true)
                                    .execute();

                            if (response.statusCode() == 200) {

                                Document document = response.parse();

                                String attrCannoical = document.select("head link[rel=\"canonical\"]").attr("abs:href");

                                String canocialTag = Urlnorm.norm(attrCannoical);


                                if (attrCannoical.equals("") || (!visited.contains(canocialTag) && !canocialTag.equals("INVALID_URL"))) {

                                    visited.add(canocialTag);

                                    Elements links = linkExtractor(document);

                                    //Elements links2 = document.select(".g>.r>a");
                                    //absolute Links
                                    LinkedList<String> rel_links = new LinkedList<>();

                                    links.stream().forEach(l -> {

                                        String linkAbsoluteURL = l.attr("abs:href");

                                        if (!linkAbsoluteURL.isEmpty() && !linkAbsoluteURL.toLowerCase().contains("web.archive")) {

                                            rel_links.add(linkAbsoluteURL);

                                            String linkNormalizedURL = Urlnorm.norm(linkAbsoluteURL);

                                            if (!linkNormalizedURL.equals("INVALID_URL") && !visited.contains(linkNormalizedURL)) {
                                                if (tempQueueMap.containsKey(linkNormalizedURL)) {
                                                    QueueElement queueElement = tempQueueMap.get(linkNormalizedURL);
                                                    queueElement.increaseInlinks(1);
                                                    tempQueueMap.put(linkNormalizedURL, queueElement);

                                                } else {
                                                    QueueElement queueElement = new QueueElement(linkAbsoluteURL,
                                                            linkNormalizedURL,
                                                            1,
                                                            System.currentTimeMillis(),
                                                            calculateScore(), getKeywordsCount(l));
                                                    tempQueueMap.put(linkNormalizedURL, queueElement);

                                                }
                                            }
                                        }
                                    });
                                    String jsonFile = "invalid_data";
                                    if (attrCannoical.equals("")) {
                                        jsonFile = JsonGenerator.getJsonObject(document, "hw3", "ssk", response.headers(), rel_links, 0, surl, normFetch, document.title());
                                        getAuthor("ssk", "document", normFetch, jsonFile);
                                    } else {
                                        jsonFile = JsonGenerator.getJsonObject(document, "hw3", "ssk", response.headers(), rel_links, 0, surl, canocialTag, document.title());
                                        getAuthor("ssk", "document", canocialTag, jsonFile);

                                    }
                                }


                            }

                        }

                        visited.add(normFetch);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            }

            visited.add(normalisedURL);

        });

        AtomicInteger depth = new AtomicInteger(1);

        // Dumping tempmap to priority queue

        tempQueueMap.forEach((link, queueElem) -> {

            if (queueElem.getNoOfkeywords() >= noOFKey.get())
                linksQueue.offer(queueElem);


        });

        tempQueueMap.clear();

        while (linksQueue.size() > 0 && noOfDocuments.intValue() < 50000) {

            String poll_url = linksQueue.poll().getURL();
            System.out.println("\nTrying to crawl: " + poll_url + "   depth:" + depth);
            String normalisedURL = Urlnorm.norm(poll_url);
            System.out.println("After nomr: " + normalisedURL);

            if (!normalisedURL.equals("INVALID_URL") && !visited.contains(normalisedURL)) {


                if (checkRobot(poll_url)) {


                    try {

                        checkForPoliteness(politenessMap, poll_url);


                        String fetchedUrl = getFinalRedirectedUrl(Urlnorm.decode(poll_url));
                        System.out.println("FetchedURL is:" + Urlnorm.decode(fetchedUrl));
                        String normFetch = Urlnorm.norm(fetchedUrl);
                        if (!visited.contains(Urlnorm.norm(normFetch))) {

//                            visited.add(normalisedURL);
                            //visited.add(Urlnorm.norm(fetchedUrl));

                            Connection.Response response = Jsoup.connect(Urlnorm.decode(fetchedUrl))
                                    //.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2 Firefox/25.0")
                                    .referrer("http://www.google.com")
                                    .timeout(7000)
                                    .followRedirects(true)
                                    .execute();


                            try {
                                politenessMap.put(new URL(poll_url).getHost().toLowerCase().replaceFirst("www.", ""), System.currentTimeMillis());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }

                            if (response.statusCode() == 200) {

                                Document document = response.parse();


                                String attrCannoical = document.select("head link[rel=\"canonical\"]").attr("abs:href");

                                String canocialTag = Urlnorm.norm(attrCannoical);

                                if (attrCannoical.equals("") || (!visited.contains(canocialTag) && !canocialTag.equals("INVALID_URL"))) {

                                    visited.add(canocialTag);

                                    Elements links = linkExtractor(document);

                                    //Elements links2 = document.select(".g>.r>a");
                                    //absolute Links
                                    LinkedList<String> rel_links = new LinkedList<>();

                                    links.stream().forEach(l -> {

                                        String linkAbsoluteURL = l.attr("abs:href");

                                        if (!linkAbsoluteURL.isEmpty() && !linkAbsoluteURL.toLowerCase().contains("web.archive")) {

                                            rel_links.add(linkAbsoluteURL);

                                            String linkNormalizedURL = Urlnorm.norm(linkAbsoluteURL);

                                            if (!linkNormalizedURL.equals("INVALID_URL") && !visited.contains(linkNormalizedURL) && !linkNormalizedURL.equals(normalisedURL)) {
                                                if (tempQueueMap.containsKey(linkNormalizedURL)) {
                                                    QueueElement queueElement = tempQueueMap.get(linkNormalizedURL);
                                                    queueElement.increaseInlinks(1);
                                                    tempQueueMap.put(linkNormalizedURL, queueElement);

                                                } else {
                                                    QueueElement queueElement = new QueueElement(linkAbsoluteURL,
                                                            linkNormalizedURL,
                                                            1,
                                                            System.currentTimeMillis(),
                                                            calculateScore(), getKeywordsCount(l));
                                                    tempQueueMap.put(linkNormalizedURL, queueElement);

                                                }
                                            }
                                        }
                                    });

                                    String jsonFile = "invalid_data";
                                    if (attrCannoical.equals("")) {
                                        jsonFile = JsonGenerator.getJsonObject(document, "hw3", "ssk", response.headers(), rel_links, 0, poll_url, normFetch, document.title());
                                        getAuthor("ssk", "document", normFetch, jsonFile);
                                    } else {
                                        jsonFile = JsonGenerator.getJsonObject(document, "hw3", "ssk", response.headers(), rel_links, 0, poll_url, canocialTag, document.title());
                                        getAuthor("ssk", "document", canocialTag, jsonFile);

                                    }


                                    bw.write(jsonFile);

                                    noOfDocuments.addAndGet(1);
                                    System.out.println("Crawled number of documents: " + noOfDocuments.get() + "\n");

                                }
                            }


                        }
                        visited.add(normFetch);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Inside exceptioon");
                    }


                }


            }

            visited.add(normalisedURL);

            if (linksQueue.isEmpty()) {
                System.out.println("Inside empty");
                //noOFKey.incrementAndGet();
                tempQueueMap.forEach((link, queueElem) -> {

                    if (queueElem.getNoOfkeywords() >= 3)
                        linksQueue.offer(queueElem);


                });

                tempQueueMap.clear();
                depth.addAndGet(1);


            }

        }

    }

    private static Double calculateScore() {
        return 0.0;
    }

    private static Integer getKeywordsCount(Element l) {
        AtomicInteger count = new AtomicInteger(0);

        URL url = null;
        String path = "";
        try {
            url = new URL(Urlnorm.decode(l.attr("abs:href").toLowerCase()));
            path = Urlnorm.decode(url.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String finalPath = path;
        String text = l.text().toLowerCase();
        relavantList.stream().forEach(rel -> {

            if (finalPath.contains(rel) || text.contains(rel)) {
                count.incrementAndGet();
            }
        });

        return count.get();
    }

    private static void checkForPoliteness(Map<String, Long> politenessMap, String poll_url) {

        String host = null;
        try {
            host = new URL(poll_url).getHost().toLowerCase().replaceFirst("www.", "");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (politenessMap.containsKey(host)) {

            long diff = System.currentTimeMillis() - politenessMap.get(host);

            if (diff < delay) {

                try {
                    Thread.sleep(delay - diff);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static LinkedList<String> absoulteURL(Elements links) {
        LinkedList<String> urls = new LinkedList<>();
        links.stream().forEach(l -> {
            urls.add(l.attr("abs:href"));
        });

        return urls;
    }

    private static Elements linkExtractor(Document document) {


//        String filteredLinksCssQuery = "a[href]:not([href~=(?i)\\.jpe?g$])" +
//                                                ":not([href~=(?i)\\.png$])" +
//                                                ":not([href~=(?i)\\.pdf$])" +
//                                                ":not([href~=(?i)\\.xml$])" +
//                                                ":not([href~=(?i)\\.css$])" +
//                                                ":not([href~=(?i)\\.js$])" +
//                                                ":not([href~=(?i)\\.gif$])" +
//                                                ":not([href~=(?i)^#])";

//        String filteredLinksCssQuery = "a[href]:not([href~=(?i).*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
//                                                                "|rm|smil|wmv|swf|wma|zip|rar|gz|csv|xls|ppt|doc|docx|exe|dmg|midi|mid|qt|txt|ram|json))$)"  +
//                                                                ":not([href~=(?i)^#])";
//        Elements links = document.select("a[href]");
//        Elements links2 = document.select(filteredLinksCssQuery); // a with href
//        Elements jpgFiles = document.select("a[href~=(?i)\\.jpe?g$]");
//        Elements pngFiles = document.select("a[href~=(?i)\\.png$]");
//        Elements pdfFiles = document.select("a[href~=(?i)\\.pdf$]");
//        Elements xmlfiles = document.select("a[href~=(?i)\\.xml$]");
//        Elements cssfiles = document.select("a[href~=(?i)\\.css$]");
//        Elements jsfiles = document.select("a[href~=(?i)\\.js$]");
//        Elements gifFiles = document.select("a[href~=(?i)\\.gif$]");
//        Elements hashFiles = document.select("a[href~=(?i)^#]");

        return document.select(cssLinkSelector);


    }

    private static boolean checkRobot(String surl) {


        String USER_AGENT = "WhateverBot";
        String url = surl;
        URL urlObj = null;
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
        String hostId = urlObj.getProtocol() + "://" + urlObj.getHost()
                + (urlObj.getPort() > -1 ? ":" + urlObj.getPort() : "");
        BaseRobotRules rules = robotsTxtRules.get(hostId);
        if (rules == null) {

            try {
                HttpGet httpget = new HttpGet(hostId + "/robots.txt");
                HttpContext context = new BasicHttpContext();
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httpget, context);
                } catch (IOException e) {
                    e.printStackTrace();
                    return true;
                }
                if (response.getStatusLine() != null && response.getStatusLine().getStatusCode() == 404) {
                    rules = new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_ALL);
                    // consume entity to deallocate connection
                    EntityUtils.consumeQuietly(response.getEntity());
                } else {

                    try {
                        BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
                        SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
                        rules = robotParser.parseContent(hostId, IOUtils.toByteArray(entity.getContent()),
                                "text/plain", USER_AGENT);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return true;
                    }
                }
                robotsTxtRules.put(hostId, rules);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (rules == null) {
            delay = 700;
            return true;
        }
        boolean urlAllowed = rules.isAllowed(url);
        long crawlDelay = rules.getCrawlDelay();
        if (crawlDelay < 0) {
            delay = 700;
        } else {
            delay = crawlDelay;
        }


        return urlAllowed;

    }

    public static String getFinalRedirectedUrl(String url) {

        HttpURLConnection connection;
        String finalUrl = url;
        int count = 0;
        try {
            do {
                connection = (HttpURLConnection) new URL(finalUrl)
                        .openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setUseCaches(false);
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode >= 300 && responseCode < 400) {


                    String redirectedUrl = connection.getHeaderField("Location");
                    URL url1 = connection.getURL();
                    if (redirectedUrl.startsWith("/"))
                        redirectedUrl = url1.getProtocol() + "://" + url1.getHost() + redirectedUrl;


                    if (redirectedUrl.startsWith("www"))
                        redirectedUrl = "http://" + redirectedUrl;

                    if (null == redirectedUrl || count == 5)
                        break;
                    finalUrl = redirectedUrl;
                    count += 1;
                    //System.out.println("redirected url: " + finalUrl);
                } else
                    break;
            } while (connection.getResponseCode() != HttpURLConnection.HTTP_OK);
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalUrl;
    }

    private static Client getTransportESClient() {
        if (_Tclient == null) {

            try {
                Settings settings = Settings.builder()
                        .put("cluster.name", "bazinga").build();
                _Tclient = new PreBuiltTransportClient(settings)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));


            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return _Tclient;
    }

    public static String getAuthor(String indexName, String type, String docNo, String JSON) {
        Client client = null;

        try {
            client = getTransportESClient();

            GetResponse response = client.prepareGet(indexName, type, docNo)
                    .setFetchSource(new String[]{"docno", "author"}, null)
                    .get();


            if (response.isExists()) {

                System.out.println("Trying to merge DocNo: " + docNo);

                String newAuthor = response.getSource().get("author").toString() + " Sushant";

                UpdateRequest updateRequest = new UpdateRequest(indexName, type, docNo)
                        .script(new Script("ctx._source.author = \"" + newAuthor + "\"")).timeout("5000ms");

                UpdateResponse updateResponse = client.update(updateRequest).get();

                if (updateResponse.status().getStatus() == 200)
                    System.out.println("Merged Successfully, DocNo: " + docNo);
                else
                    System.out.println("Failed to Merge, DocNo: " + docNo);

                //mergedOne.incrementAndGet();

            } else {

                System.out.println("Trying to insert DocNo: " + docNo);
                IndexResponse indexResponse = client.prepareIndex(indexName, type, docNo)
                        .setSource(JSON)
                        .get();
//                System.out.println(indexResponse.status());
//                System.out.println(indexResponse.status().getStatus());
                if (indexResponse.status().getStatus() == 201)
                    System.out.println("Indexed Successfully, DocNo: " + docNo);
                else
                    System.out.println("Failed to Index, DocNo: " + docNo);

                //insertOne.incrementAndGet();

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return null;
    }

    public static void main(String[] args) {

        boolean b = checkRobot(" http://facebook.com/charismanews");
        checkRobot("http://www.csnchicago.com/user");


    }
}
