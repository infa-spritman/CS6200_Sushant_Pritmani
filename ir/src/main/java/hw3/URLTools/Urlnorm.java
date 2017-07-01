package hw3.URLTools;

import com.googlecode.totallylazy.Sequences;
import org.apache.http.client.utils.URIBuilder;

import com.googlecode.totallylazy.regex.Regex;
import static com.googlecode.totallylazy.regex.Regex.regex;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.net.*;

/**
 * Created by Sushant on 6/21/2017.
 */
public class Urlnorm {

    private final Pattern server_authority = Pattern.compile("^(?:([^\\@]+)\\@)?([^\\:\\[\\]]+|\\[[a-fA-F0-9\\:\\.]+\\])(?:\\:(.*?))?$");
    public static final String[] ALLOWED_PORTOCOLS = new String[] { "http", "https" };
    public static final Set<String> PROTOCOL_SET = new HashSet<String>(Arrays.asList(ALLOWED_PORTOCOLS));
    private static final Regex segment = regex("/?([^/]+)");



    public static String norm(String url) {

        try {
            URL url1 = new URL(decode(url));
            return normalizeUrl(url1);

        } catch (Exception e) {
            System.out.println("invalid URl: " + url);
            e.printStackTrace();
        }

        return "INVALID_URL";
    }

    private static String normalizeUrl(URL url1) {

        StringBuilder sb = new StringBuilder();
//        System.out.println("protocol = " + url1.getProtocol());
//        System.out.println("authority = " + url1.getAuthority());
//        System.out.println("host = " + url1.getHost());
//        System.out.println("port = " + url1.getPort());
//        System.out.println("path = " + url1.getPath());
//        System.out.println("query = " + url1.getQuery());
//        System.out.println("filename = " + url1.getFile());
//        System.out.println("ref = " + url1.getRef());

        String protocol = url1.getProtocol().toLowerCase();
        String host = url1.getHost().toLowerCase().replaceFirst("www.","");
        if(!PROTOCOL_SET.contains(protocol))
            return "INVALID_URL";

        sb.append("http://");
        sb.append(host);

        if(url1.getPath() != null && !url1.getPath().isEmpty())
            sb.append(normPath(url1.getPath()));

        if(url1.getQuery()!= null && !url1.getQuery().isEmpty())
            sb.append("?"+ url1.getQuery());

//        if(host.contains("google") && url1.getRef()!= null && !url1.getRef().isEmpty())
//            sb.append("#"+url1.getRef());

        if(sb.charAt(sb.length()-1) == '/')
            sb.setLength(sb.length() - 1);


        return sb.toString().toLowerCase();
    }

    private static String normPath(String path) {

        final Deque<CharSequence> segments = new ArrayDeque<CharSequence>();
        segment.findMatches(path).replace(notMatched -> {
            segments.add(notMatched);
            return null;
        }, match -> {
            switch (match.group(1)) {
                case ".":
                    return null;
                case "..":
                    if (!segments.isEmpty()) segments.removeLast();
                    break;
                default:
                    segments.add(match.group());
                    break;
            }
            return null;
        });
        return Sequences.toString(segments, "");
    }

    private static URI createURI(URL url){
        URI uri = null;
        try {
            uri = new URI(url.getProtocol(),
                    null /*userInfo*/,
                    url.getHost(),
                    url.getPort(),
                    url.getFile(),
                    url.getQuery(),
                    null /*fragment*/);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;


    }

    public static String decode(String value) {
        String decoded
                = null;
        try {
            decoded = URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decoded;
    }

    public static void main(String[] args) throws Exception {

        System.out.println(norm( "https://web.archive.org/web/20061126122254/https://www.brookings.edu/metro/immigration.htm"));
    }

    public static String encode(String poll_url) {

        String encoded
                = null;
        try {
            encoded = URLEncoder.encode(poll_url, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoded;
    }
}
