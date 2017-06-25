package hw3.Start;


import hw3.Crawler.CrawlWebs;

import java.util.ArrayList;

/**
 * Created by Sushant on 6/22/2017.
 */
public class CrawlRunner {


    public static void main(String[] args){

        String dir = "C:\\Users\\Sushant\\Desktop\\IR\\ResultAssignment3\\";

        ArrayList<String> seedURls  = new ArrayList<>();

        seedURls.add("http://en.wikipedia.org/wiki/Immigration_to_the_United_States");
//        seedURls.add("https://www.google.com/search?q=" + "IMMIGRATION TO UNITED STATES donald trump" + "&num=20");
//        seedURls.add("https://www.google.com/search?q=" +"recent+immigration+obama&tbm=nws");
//        seedURls.add("http://www.charismanews.com/us/39838-southern-baptist-churches-growing-in-numbers-declining-in-membership");

        CrawlWebs.crawl(seedURls,dir);





    }
}
