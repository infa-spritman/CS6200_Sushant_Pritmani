package com.googlecode.totallylazy.parser;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Segment;

import static com.googlecode.totallylazy.Unchecked.cast;

class MappingParser<A, B> implements Parser<B> {
    private final Parser<? extends A> source;
    private final Function1<? super A, ? extends B> callable;

    private MappingParser(Parser<? extends A> source, Function1<? super A, ? extends B> callable) {
        this.source = source;
        this.callable = callable;
    }

    public static <A, B> MappingParser<A, B> map(Parser<? extends A> source, Function1<? super A, ? extends B> callable) {
        return new MappingParser<A, B>(source, callable);
    }

    @Override
    public Result<B> parse(Segment<Character> characters) {
        return cast(source.parse(characters).map(callable));
    }

    @Override
    public String toString() {
        return String.format("%s %s", source, callable);
    }
}
