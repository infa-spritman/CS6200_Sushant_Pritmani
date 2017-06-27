package hw3.Start;


import hw3.Crawler.CrawlWebs;
import hw3.URLTools.Urlnorm;

import java.util.ArrayList;

/**
 * Created by Sushant on 6/22/2017.
 */
public class CrawlRunner {


    public static void main(String[] args){

        String dir = "C:\\Users\\Sushant\\Desktop\\IR\\ResultAssignment3\\";

        ArrayList<String> seedURls  = new ArrayList<>();

        seedURls.add("http://en.wikipedia.org/wiki/Immigration_to_the_United_States");
//        seedURls.add("https://www.google.com/#q=" + "IMMIGRATION TO UNITED STATES donald trump" + "&num=20");
//        seedURls.add("https://www.google.com/search?q=" +"recent+immigration+obama&tbm=nws");
//        seedURls.add("http://www.charismanews.com/us/39838-southern-baptist-churches-growing-in-numbers-declining-in-membership");
//          seedURls.add("https://doi.org/10.1007%2Fs10290-014-0191-8");
        //seedURls.add("https://doi.org/10.1016%2Fj.worlddev.2016.08.012");

//        seedURls.add("http://www.sfgate.com/cgi-bin/article.cgi?f=/c/a/2010/08/04/MN5H1ENBPK.DTL&type=politics");
//        seedURls.add("http://www.world-nuclear.org/info/Safety-and-Security/Safety-of-Plants/Fukushima-Accident/");
        CrawlWebs.crawl(seedURls,dir);





    }
}
