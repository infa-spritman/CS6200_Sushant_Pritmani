package hw3.Start;


import hw3.Crawler.CrawlWebs;

import java.util.ArrayList;

/**
 * Created by Sushant on 6/22/2017.
 */
public class CrawlRunner {


    public static void main(String[] args){

        String dir = "C://Users//Sushant//Desktop//IR//ResultAssignment3";

        ArrayList<String> seedURls  = new ArrayList<>();

        seedURls.add("http://en.wikipedia.org/wiki/Immigration_to_the_United_States");
        seedURls.add("https://www.google.com/#q=IMMIGRATION+TO+UNITED+STATES+donald+trump");
        seedURls.add("https://www.google.com/#q=recent+immigration+obama&tbm=nws");


        CrawlWebs.crawl(seedURls,dir);





    }
}
