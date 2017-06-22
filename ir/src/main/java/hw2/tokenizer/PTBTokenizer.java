package hw2.tokenizer;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sushant on 6/4/2017.
 */
public class PTBTokenizer {

    private static ArrayList<String> tokenizeText(String input) {

        String temp = new String(input);
        String processedString = temp.replaceAll("'([sSmMdD])", "")
                .replaceAll("/('ll|'LL|'re|'RE|'ve|'VE|n't|N'T) /g", " $1 ")
                .replaceAll("[\\]\\[\\(\\)\\{\\}<>]", " ")
                .replaceAll("(?![.])\\p{Punct}", " ")
                .replaceAll("(\\.\\B)+", " ")
                //.replaceAll("\n"," ")
                .toLowerCase();

//        String temp1 = temp
//                                 .replaceAll("^\"", "``")
//                                 .replaceAll("([ (\\[{<])\"", "$1 `` ")
//                                 .replaceAll("\\.\\.\\."," ... ")
//                                 .replaceAll("[;@#$%&]"," ")
//                                 .replaceAll("([^\\.])(\\.)([\\]\\)}>\"\\']*)\\s*$","$1 $2$3 ")
//                                 .replaceAll("[?!]"," ")
//                                 .replaceAll("[\\]\\[\\(\\)\\{\\}<>]"," ")
//                                 .replaceAll("--"," -- ");
//
//        String temp2 = " "+temp1+" ";
//
//        String processedString = temp2.replaceAll("\""," \\'\\' ")
//                                        .replaceAll("([^'])' ","$1 \\' ")
//                                        .replaceAll("'([sSmMdD])", " ")
//                                        .replaceAll("/('ll|'LL|'re|'RE|'ve|'VE|n't|N'T) /g"," $1 ")
//                                        .replaceAll("\\ \\ +"," ")
//                                        .replaceAll("^\\ |\\ $"," ");

        return new ArrayList<>(Arrays.asList(processedString.split(" ")));

    }

    public static LinkedList<TokenObject> tokenize(String input, String docId) {

        AtomicInteger atomicInteger = new AtomicInteger(1);

        ArrayList<String> tokens = tokenizeText(input);
        LinkedList<TokenObject> tokenObjects = new LinkedList<>();

        tokens.forEach(t -> {
            if(!t.isEmpty())
                tokenObjects.add(new TokenObject(t, docId, Integer.toString(atomicInteger.getAndIncrement())));
        });

        return tokenObjects;
    }


    public static LinkedList<TokenObject> tokenizeHead(List<String> headList, String docId) {

        AtomicInteger atomicInteger = new AtomicInteger(1);
        LinkedList<TokenObject> tokenObjects = new LinkedList<>();


        headList.forEach(ht->{
            ArrayList<String> tokens = tokenizeText(ht);

            tokens.forEach(t -> {
                if(!t.isEmpty())
                    tokenObjects.add(new TokenObject(t, docId, Integer.toString(atomicInteger.getAndIncrement())));
            });

        });



        return tokenObjects;
    }

    public static void main(String[] args){

        String input ="u.s.a algorithm's they're boys girls and the hobbies they've i haven't 192.0.0.1  hello. !@#$%^&()-={}'|\\//<>.,~`-+";
        LinkedList<TokenObject> doc101 = tokenize(input, "doc101");

        for(TokenObject t : doc101){

            System.out.println(t.getTermId()+","+t.getDocId()+","+ t.getPosition());
        }

//        ArrayList<String> strings = tokenizeText(input);
//        for(String s: strings){
//            System.out.println("<"+s+">");
//        }
    }
}
