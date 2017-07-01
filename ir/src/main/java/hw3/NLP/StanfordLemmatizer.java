package hw3.NLP;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import hw2.StanfordStemmer.CustomStemmer;
import hw2.tokenizer.PTBTokenizer;
import hw2.tokenizer.TokenObject;

public class StanfordLemmatizer {


    public static void main(String[] args) {

        StringBuilder s = new StringBuilder();
        CustomStemmer cs  = new CustomStemmer();
        s.append("immigration " +
                "donaldtrump " +
                "visa " +
                "green card " +
                "illegal " +
                "citizenship " +
                "canada " +
                "obama " +
                "donald " +
                "trump " +
                "rules " +
                "foreign " +
                "immigrants " +
                "speech " +
                "us " +
                "usa " +
                "united " +
                "states " +
                "reform " +
                "barack " +
                "law " +
                "act " +
                "bill " +
                "history " +
                "deportation " +
                "order " +
                "policy " +
                "executive " +
                "ban " +
                "mexican " +
                "uscis " +
                "refugee " +
                "homeland " +
                "naturalization " +
                "america " +
                "U.S. " +
                "cross " +
                "border " +
                "migrant " +
                "mexico " +
                "repatri.");
        LinkedList<TokenObject> tokenize = PTBTokenizer.tokenize(s.toString(),"sample");
        tokenize.forEach(t -> {
            System.out.println(cs.stem(t.getTermId()));

        });
    }

}
