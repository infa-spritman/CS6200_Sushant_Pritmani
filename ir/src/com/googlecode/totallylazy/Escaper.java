package com.googlecode.totallylazy;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.Predicate;

import static com.googlecode.totallylazy.functions.Callables.toString;
import static com.googlecode.totallylazy.functions.Functions.returns1;
import static com.googlecode.totallylazy.predicates.Predicates.always;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.Sequences.characters;

public class Escaper {
    private final Rules<Character, String> rules = Rules.rules();

    public Escaper() {
        rules.addLast(always(Character.class), toString);
    }

    public Escaper withRule(Character appliesTo, final String result) {
        return withRule(is(appliesTo), returns1(result));
    }

    public Escaper withRule(Predicate<? super Character> appliesTo, Function1<? super Character, ? extends String> action) {
        rules.addFirst(appliesTo, action);
        return this;
    }

    public String escape(Object value) {
        return value == null ? null : characters(value.toString()).map(escape()).toString("");
    }

    private Function1<Character, String> escape() {
        return character -> rules.apply(character);
    }

}
