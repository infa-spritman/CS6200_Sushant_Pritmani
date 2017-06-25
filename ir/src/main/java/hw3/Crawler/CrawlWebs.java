package hw3.Crawler;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
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
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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


    static BufferedWriter bw = null;
    static java.io.FileWriter fw = null;

    static Queue<String> linkQueue ;
    static LinkedList<String> secondaryQueue = new LinkedList<>();
    static Map<String,Long> politenessMap = new HashMap<>();


    public static void crawl(ArrayList<String> seedURls, String dir) {

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

                    visited.add(normalisedURL);

                    try {
                        politenessMap.put(new URL(surl).getHost().toLowerCase().replaceFirst("www.",""),System.currentTimeMillis());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    try {

                        Connection.Response response = Jsoup.connect(surl).userAgent(
                                "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)").execute();


                        if (response.statusCode() == 200) {

                            Document document = response.parse();

                            Elements links = linkExtractor(document);

                            //Elements links2 = document.select(".g>.r>a");
                            //absolute Links
                            LinkedList<String> rel_links = absoulteURL(links);

                            linkQueue = rel_links;

                            String jsonFile = JsonGenerator.getJsonObject(document, "hw3", "ssk", response.headers(), rel_links, 0, surl, normalisedURL, document.title());

                            bw.write(jsonFile);

                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }


            }

        });

        AtomicInteger depth = new AtomicInteger(1);


        while(linkQueue.size()>0 && noOfDocuments.intValue()<20000){

            String poll_url = linkQueue.poll();
            System.out.println("Trying to crawl: " + poll_url + "   depth:" + depth );
            String normalisedURL = Urlnorm.norm(poll_url);
            System.out.println("After nomr" + poll_url);

            if (!normalisedURL.equals("INVALID_URL") && !visited.contains(normalisedURL)) {


                if (checkRobot(poll_url)) {

                    System.out.println("After checking robot" + poll_url);

                    try {

                        checkForPoliteness(politenessMap,poll_url);

                        System.out.println("Crawling url(after politecheck) : " + poll_url);

                        Connection.Response response = Jsoup.connect(Urlnorm.decode(poll_url))
                                //.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2")
                                .referrer("http://www.google.com")
                                .timeout(12000)
                                .followRedirects(true)
                                .execute();

                        try {
                            politenessMap.put(new URL(poll_url).getHost().toLowerCase().replaceFirst("www.",""),System.currentTimeMillis());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        if (response.statusCode() == 200) {

                            Document document = response.parse();

                            Elements links = linkExtractor(document);

                            //Elements links2 = document.select(".g>.r>a");
                            //absolute Links
                            LinkedList<String> rel_links = absoulteURL(links);

                            secondaryQueue.addAll(rel_links);
                            //String jsonFile = JsonGenerator.getJsonObject(document, "hw3", "ssk", response.headers(), rel_links, 0, surl, normalisedURL, document.title());

                            //bw.write(jsonFile);

                        }

                        visited.add(normalisedURL);
                        noOfDocuments.addAndGet(1);
                        System.out.println("Crawled number of documents: " + noOfDocuments.get() + "\n");


                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Inside exceptioon");
                    }


                }


            }

            if(linkQueue.isEmpty()){
                System.out.println("Inside empty");
                LinkedList<String> temp = new LinkedList<>();
                temp.addAll(secondaryQueue);
                linkQueue = temp;
                depth.addAndGet(1);
                secondaryQueue.clear();
            }

        }

    }

    private static void checkForPoliteness(Map<String, Long> politenessMap, String poll_url) {

        String host = null;
        try {
            host = new URL(poll_url).getHost().toLowerCase().replaceFirst("www.", "");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if(politenessMap.containsKey(host)){

            long diff = System.currentTimeMillis() - politenessMap.get(host);

            if( diff <1000){

                try {
                    Thread.sleep( 1000 - diff);
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
        }
        boolean urlAllowed = rules.isAllowed(url);

        return urlAllowed;

    }


    public static void main(String[] args) {

        boolean b = checkRobot(" http://facebook.com/charismanews");
        System.out.println(b);

    }
}
