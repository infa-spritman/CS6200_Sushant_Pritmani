package com.googlecode.totallylazy.template.ast;

import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.parser.Parser;
import com.googlecode.totallylazy.parser.Parsers;
import com.googlecode.totallylazy.predicates.Predicate;

import java.util.List;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Arrays.list;
import static com.googlecode.totallylazy.Characters.alphaNumeric;
import static com.googlecode.totallylazy.parser.Parsers.between;
import static com.googlecode.totallylazy.parser.Parsers.characters;
import static com.googlecode.totallylazy.parser.Parsers.isChar;
import static com.googlecode.totallylazy.parser.Parsers.or;
import static com.googlecode.totallylazy.parser.Parsers.ws;
import static com.googlecode.totallylazy.parser.Parsers.wsChar;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.not;

public class Grammar {
    static Grammar Default = new Grammar();

    public static Parser<List<Expression>> parser() {
        return Default.TEMPLATE;
    }

    public static Parser<List<Expression>> parser(char delimiter) {
        return new Grammar() {
            @Override char DELIMITER() { return delimiter; }
        }.TEMPLATE;
    }

    char DELIMITER() { return '$'; }

    Parser<Void> SEPARATOR = wsChar(',').ignore();
    Parser<Void> SINGLE_QUOTE = isChar('\'').ignore();
    Parser<Void> DOUBLE_QUOTE = isChar('"').ignore();

    Parser<String> IDENTIFIER = Parsers.characters(alphaNumeric).map(Object::toString);

    Parser<Name> NAME = IDENTIFIER.map(Name::name);
    Parser<Text> TEXT = textExcept(is(DELIMITER()));
    Parser<Text> SINGLE_QUOTED = between(SINGLE_QUOTE, textExcept('\''), SINGLE_QUOTE);
    Parser<Text> DOUBLE_QUOTED = between(DOUBLE_QUOTE, textExcept('"'), DOUBLE_QUOTE);
    Parser<Text> LITERAL = SINGLE_QUOTED.or(DOUBLE_QUOTED);

    static Parser<Text> textExcept(char c) {
        return textExcept(is(c));
    }

    static Parser<Text> textExcept(Predicate<Character> predicate) {
        return Parsers.characters(not(predicate)).map(Text::text);
    }

    Parser<Expression> VALUE = Parsers.lazy(new Callable<Parser<Expression>>() {
        @Override
        public Parser<Expression> call() throws Exception {
            return ws(or(LITERAL, FUNCTION_CALL, ATTRIBUTE));
        }
    });

    Parser<Expression> EXPRESSION = Parsers.lazy(new Callable<Parser<Expression>>() {
        @Override
        public Parser<Expression> call() throws Exception {
            return Parsers.<Expression>or(FUNCTION_CALL, MAPPING, ATTRIBUTE).surroundedBy(isChar(DELIMITER()));
        }
    });

    Parser<Expression> NAMES = Parsers.lazy(new Callable<Parser<Expression>>() {
        @Override
        public Parser<Expression> call() throws Exception {
            return or(NAME, INDIRECTION);
        }
    });

    Parser<Attribute> ATTRIBUTE = NAMES.sepBy1(isChar('.')).map(Attribute::attribute);

    Parser<Pair<String, Expression>> NAMED_ARGUMENT = IDENTIFIER.followedBy(isChar('=')).then(VALUE);

    Parser<NamedArguments> NAMED_ARGUMENTS = NAMED_ARGUMENT.sepBy1(SEPARATOR).map(Maps::map).map(NamedArguments::namedArguments);

    Parser<ImplicitArguments> IMPLICIT_ARGUMENTS = VALUE.sepBy(SEPARATOR).map(ImplicitArguments::implicitArguments);


    Parser<FunctionCall> FUNCTION_CALL =
            NAMES.then(between(isChar('('), or(NAMED_ARGUMENTS, IMPLICIT_ARGUMENTS), isChar(')'))).
                    map(pair -> FunctionCall.functionCall(pair.first(), pair.second()));

    Parser<List<Expression>> TEMPLATE = or(EXPRESSION, TEXT).many1();

    Parser<List<String>> PARAMETERS_NAMES = IDENTIFIER.sepBy1(SEPARATOR);
    Parser<Anonymous> ANONYMOUS_TEMPLATE = between(wsChar('{'),
            PARAMETERS_NAMES.followedBy(wsChar('|')).optional().map(o -> o.getOrElse(list())).
                    then(characters(not('}')).flatMap(c -> TEMPLATE.parse(c))), wsChar('}')).
            map(pair -> Anonymous.anonymous(pair.first(), pair.second()));

    Parser<Mapping> MAPPING = ATTRIBUTE.followedBy(isChar(':')).then(ANONYMOUS_TEMPLATE).
            map(pair -> Mapping.mapping(pair.first(), pair.second()));

    Parser<Indirection> INDIRECTION = Parsers.<Expression>or(ATTRIBUTE, ANONYMOUS_TEMPLATE, LITERAL).between(isChar('('), isChar(')')).
            map(Indirection::indirection);
}