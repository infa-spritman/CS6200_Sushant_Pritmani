package com.googlecode.totallylazy.collections;

import com.googlecode.totallylazy.Key;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.GenericType;
import com.googlecode.totallylazy.Unchecked;
import com.googlecode.totallylazy.reflection.Declaration;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

public interface Keyword<T> extends Function1<Map<String, Object>, T>, GenericType<T>, Selection, Key<String> {
    String key();

    @Override
    default T call(Map<String, Object> map) throws Exception {
        Object value = map.get(key());
        return forClass().cast(value);
    }

    @Override
    default PersistentMap<String, Object> select(PersistentMap<String, Object> source, PersistentMap<String, Object> destination) {
        T value = apply(source);
        return destination.insert(key(), value);
    }

    static <T> Keyword<T> keyword(String name, Class<T> aClass) {
        return new Keyword<T>() {
            @Override
            public String key() {
                return name;
            }

            @Override
            public Class<T> forClass() {
                return aClass;
            }
        };
    }

    static <T> Keyword<T> keyword() {
        Declaration declaration = Declaration.declaration();
        ParameterizedType parameterizedType = (ParameterizedType) declaration.type();

        return Keyword.keyword(declaration.name(), Unchecked.<Class <T>>cast(parameterizedType.getActualTypeArguments()[0]));
    }

}
