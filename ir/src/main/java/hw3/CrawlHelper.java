package hw3;

/**
 * Created by Sushant on 7/2/2017.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.base.Joiner;
import com.google.common.net.HttpHeaders;
import org.jsoup.select.Elements;

public class CrawlHelper {

    /**
     * Get end contents of a urlString. Status code is not checked here because
     * org.apache.http.client.HttpClient effectively handles the 301 redirects.
     *
     * Javascript is extracted using Jsoup, and checked for references to
     * &quot;window.location.replace&quot;.
     *
     * @param urlString Url. &quot;http&quot; will be prepended if https or http not already there.
     * @return Result after all redirects, including javascript.
     * @throws IOException
     */
    public String getResult(final String urlString) throws IOException {
        String html = getTextFromUrl(urlString);
        Document doc = Jsoup.parse(html);
        for (Element script : doc.select("script")) {
            String potentialURL = getTargetLocationFromScript(urlString, script.html());
            if (potentialURL.indexOf("/") == 0) {
                potentialURL = Joiner.on("").join(urlString, potentialURL);
            }
            if (!StringUtil.isBlank(potentialURL)) {
                return getTextFromUrl(potentialURL);
            }
        }
        return html;
    }

    /**
     *
     * @param urlString Will be prepended if the target location doesn't start with &quot;http&quot;.
     * @param js Javascript to scan.
     * @return Target that matches window.location.replace or window.location.href assignments.
     * @throws IOException
     */
    String getTargetLocationFromScript(String urlString, String js) throws IOException {
        String potentialURL = getTargetLocationFromScript(js);
        if (potentialURL.indexOf("http") == 0) {
            return potentialURL;
        }
        return Joiner.on("").join(urlString, potentialURL);
    }

    String getTargetLocationFromScript(String js) throws IOException {
        int i = js.indexOf("window.location.replace");
        if (i > -1) {
            return getTargetLocationFromLocationReplace(js);
        }
        i = js.indexOf("window.location.href");
        if (i > -1) {
            return getTargetLocationFromHrefAssign(js);
        }
        return "";
    }

    private String getTargetLocationFromHrefAssign(String js) {
        return findTargetFrom("window.location.href\\s?=\\s?\\\"(.+)\\\"", js);
    }

    private String getTargetLocationFromLocationReplace(String js) throws IOException {
        return findTargetFrom("window.location.replace\\(\\\"(.+)\\\"\\)", js);
    }

    private String findTargetFrom(String regex, String js) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(js);
        while (m.find()) {
            String potentialURL = m.group(1);
            if (!StringUtil.isBlank(potentialURL)) {
                return potentialURL;
            }
        }
        return "";
    }

    private String getTextFromUrl(String urlString) throws IOException {
        if (StringUtil.isBlank(urlString)) {
            throw new IOException("Supplied URL value is empty.");
        }
        String httpUrlString = prependHTTPifNecessary(urlString);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(httpUrlString);
        request.addHeader("User-Agent", HttpHeaders.USER_AGENT);
        HttpResponse response = client.execute(request);
        try (BufferedReader rd =
                     new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            StringWriter result = new StringWriter();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    private String prependHTTPifNecessary(String urlString) throws IOException {
        if (urlString.indexOf("http") != 0) {
            return Joiner.on("://").join("http", urlString);
        }
        return validateURL(urlString);
    }

    private String validateURL(String urlString) throws IOException {
        try {
            new URL(urlString);
        } catch (MalformedURLException mue) {
            throw new IOException(mue);
        }
        return urlString;
    }


    public static void main(String[] args) throws IOException {

        Connection.Response execute = Jsoup.connect("https://en.wikipedia.org/wiki/Illegal_immigrant")
                .followRedirects(true) //to follow redirects
                .execute();
        Document page = execute.parse();



        System.out.println(page.select("head link[rel=\"canonical\"]").attr("abs:href"));
    }
}
