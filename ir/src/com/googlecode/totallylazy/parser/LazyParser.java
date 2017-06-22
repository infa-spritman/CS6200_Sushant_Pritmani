package com.googlecode.totallylazy.parser;

import com.googlecode.totallylazy.functions.Lazy;
import com.googlecode.totallylazy.Segment;

import java.util.concurrent.Callable;

class LazyParser<T> implements Parser<T> {
    private final Lazy<Parser<T>> value;

    private LazyParser(Lazy<Parser<T>> value) {
        this.value = value;
    }

    static <T> LazyParser<T> lazy(Callable<? extends Parser<T>> value) {
        return new LazyParser<T>(Lazy.lazy(value));
    }

    @Override
    public String toString() {
        return value.value().toString();
    }

    @Override
    public Result<T> parse(Segment<Character> characters) {
        return value.value().parse(characters);
    }
}
