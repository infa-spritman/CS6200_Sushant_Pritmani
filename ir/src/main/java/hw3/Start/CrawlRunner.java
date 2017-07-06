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
        seedURls.add("https://en.wikipedia.org/wiki/Immigration_reform");
        seedURls.add("https://en.wikipedia.org/wiki/Executive_Order_13769");
        seedURls.add("https://en.wikipedia.org/wiki/DREAM_Act");
        seedURls.add("https://en.wikipedia.org/wiki/Immigration_policy_of_Donald_Trump");
        seedURls.add("http://www.politico.com/news/immigration");
        seedURls.add("http://www.politico.com/news/dream-act");
        seedURls.add("http://www.politico.com/search?q=immigration+trump");
        seedURls.add("https://en.wikipedia.org/wiki/Illegal_immigration_to_the_United_States");
        seedURls.add("https://www.theguardian.com/us-news/2017/jan/27/donald-trump-executive-order-immigration-full-text");
        seedURls.add("https://www.whitehouse.gov/the-press-office/2017/01/27/executive-order-protecting-nation-foreign-terrorist-entry-united-states");
        seedURls.add("https://www.usatoday.com/story/news/world/2017/01/28/what-you-need-know-trumps-refugee-ban/97183112/");
        seedURls.add("https://www.usatoday.com/story/news/politics/2017/02/20/donald-trump-set-to-issue-new-revised-travel-ban-against-majority-muslim-countries/98167072/");
        seedURls.add("http://www.aljazeera.com/news/2017/05/immigrant-arrests-soar-donald-trump-170518034252370.html");
        seedURls.add("http://www.aljazeera.com/news/2017/01/protests-grow-trump-immigrant-ban-order-170129194912379.html");
        seedURls.add("http://www.npr.org/2016/08/31/491965912/5-things-to-know-about-obamas-enforcement-of-immigration-laws");
        seedURls.add("https://www.washingtonpost.com/news/wonk/wp/2014/11/19/your-complete-guide-to-obamas-immigration-order/?utm_term=.ecfbf9f0d46c");
        seedURls.add("https://learningenglish.voanews.com/a/obama-immigration-order-blocked-supreme-court-texas/3394275.html");
        seedURls.add("https://www.theatlantic.com/politics/archive/2016/08/immigration-reform-central-american-refugees/494948/");
        seedURls.add("http://www.washingtontimes.com/news/2017/jan/25/trump-eviscerates-obamas-immigration-policy/");
        seedURls.add("https://www.theatlantic.com/magazine/archive/2017/07/the-democrats-immigration-mistake/528678/");
        seedURls.add("https://www.wsws.org/en/articles/2017/06/17/immi-j17.html");
        seedURls.add("http://www.newsweek.com/whats-happening-trumps-immigration-laws-latest-who-safe-and-who-isnt-626782");
        seedURls.add("http://www.nationalreview.com/article/448904/ms-13-gang-immigration-policy-obama-encouraged");
        seedURls.add("http://www.cnbc.com/2017/06/27/tensions-rising-in-silicon-valley-over-trumps-immigration-crackdown.html");
        seedURls.add("http://www.chicagotribune.com/suburbs/post-tribune/opinion/ct-ptb-editorial-welcoming-cities-st-0625-20170624-story.html");






//        seedURls.add("https://en.wikipedia.org/wiki/Illegal_immigrant");
//        seedURls.add("http://en.wikipedia.org/wiki/Immigration_to_the_United_States");


//          seedURls.add("http://www.ncsl.org/research/immigration/states-offering-driver-s-licenses-to-immigrants.aspx");
//        seedURls.add("https://www.google.com/#q=" + "IMMIGRATION TO UNITED STATES donald trump" + "&num=20");
//        seedURls.add("https://www.google.com/search?q=" +"recent+immigration+obama&tbm=nws");
//        seedURls.add("http://www.charismanews.com/us/39838-southern-baptist-churches-growing-in-numbers-declining-in-membership");
//          seedURls.add("https://doi.org/10.1007%2Fs10290-014-0191-8");
        //seedURls.add("https://doi.org/10.1016%2Fj.worlddev.2016.08.012");

//        seedURls.add("http://www.sfgate.com/cgi-bin/article.cgi?f=/c/a/2010/08/04/MN5H1ENBPK.DTL&type=politics");
 //       seedURls.add("https://www.usatoday.com/story/news/world/2017/01/28/what-you-need-know-trumps-refugee-ban/97183112/");
        CrawlWebs.crawl(seedURls,dir);





    }
}
