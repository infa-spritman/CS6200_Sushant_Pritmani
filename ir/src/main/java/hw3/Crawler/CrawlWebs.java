package hw3.Crawler;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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


    public static void crawl(ArrayList<String> seedURls, String dir) {

        AtomicInteger noOfDocuments = new AtomicInteger(3);

        seedURls.stream().forEach(surl -> {

            //boolean urlAllowed = checkRobot(surl);
            String normalisedURL = Urlnorm.norm(surl);

            if (!visited.contains(normalisedURL)) {


                if (checkRobot(surl)) {

                    visited.add(normalisedURL);

                    try {

                        Connection.Response response = Jsoup.connect(surl).execute();

                        if (response.statusCode() == 200) {

                            Document document = response.parse();
                            Elements elements = linkExtractor(document);
                            System.out.println("");

                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }


            }

        });
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
                return false;
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
                }
            }
            robotsTxtRules.put(hostId, rules);
        }
        boolean urlAllowed = rules.isAllowed(url);

        return urlAllowed;

    }


    public static void main(String[] args) {


    }
}
