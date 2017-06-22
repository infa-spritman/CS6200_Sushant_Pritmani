package com.googlecode.totallylazy;

import com.googlecode.totallylazy.functions.Callables;
import com.googlecode.totallylazy.functions.Curried2;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;

public class Maps {
    public static <K, V> Sequence<Pair<K, V>> pairs(final Map<K, V> map) {
        return entries(map).map(Maps.<K, V>entryToPair());
    }

    public static <K, V> Function1<Map<K, V>, Sequence<Map.Entry<K, V>>> entries(Class<K> keyType, Class<V> valueType) {
        return entries();
    }

    public static <K, V> Function1<Map<K, V>, Sequence<Map.Entry<K, V>>> entries() {
        return Maps::entries;
    }

    public static <K, V> Sequence<Map.Entry<K, V>> entries(final Map<K, V> map) {
        return sequence(map.entrySet());
    }

    public static <K, V> Map<K, V> map() {
        return new LinkedHashMap<K, V>();
    }

    public static <K, V> Map<K, V> map(K key, V value) {
        return map(pair(key, value));
    }

    public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2) {
        return map(pair(key1, value1), pair(key2, value2));
    }

    public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3) {
        return map(pair(key1, value1), pair(key2, value2), pair(key3, value3));
    }

    public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
        return map(pair(key1, value1), pair(key2, value2), pair(key3, value3), pair(key4, value4));
    }

    public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
        return map(pair(key1, value1), pair(key2, value2), pair(key3, value3), pair(key4, value4), pair(key5, value5));
    }

    public static <K, V> Map<K, V> map(final Pair<? extends K, ? extends V> first) {
        return map(sequence(first));
    }

    public static <K, V> Map<K, V> map(final Pair<? extends K, ? extends V> first, final Pair<? extends K, ? extends V> second) {
        return map(sequence(first, second));
    }

    public static <K, V> Map<K, V> map(final Pair<? extends K, ? extends V> first, final Pair<? extends K, ? extends V> second, final Pair<? extends K, ? extends V> third) {
        return map(sequence(first, second, third));
    }

    public static <K, V> Map<K, V> map(final Pair<? extends K, ? extends V> first, final Pair<? extends K, ? extends V> second, final Pair<? extends K, ? extends V> third, final Pair<? extends K, ? extends V> fourth) {
        return map(sequence(first, second, third, fourth));
    }

    public static <K, V> Map<K, V> map(final Pair<? extends K, ? extends V> first, final Pair<? extends K, ? extends V> second, final Pair<? extends K, ? extends V> third, final Pair<? extends K, ? extends V> fourth, final Pair<? extends K, ? extends V> fifth) {
        return map(sequence(first, second, third, fourth, fifth));
    }

    @SafeVarargs
    public static <K, V> Map<K, V> map(final Pair<? extends K, ? extends V>... entries) {
        return map(sequence(entries));
    }

    @SafeVarargs
    public static <K, V> Map<K, V> map(final Map<K, V> seed, final Pair<? extends K, ? extends V>... entries) {
        return map(seed, sequence(entries));
    }

    public static <K, V> Map<K, V> map(final Iterable<? extends Pair<? extends K, ? extends V>> entries) {
        return map(new LinkedHashMap<K, V>(), entries);
    }

    public static <K, V> Map<K, V> map(final Map<K, V> seed, final Iterable<? extends Pair<? extends K, ? extends V>> entries) {
        return map(seed, entries.iterator());
    }

    public static <K, V> Map<K, V> map(final Iterator<? extends Pair<? extends K, ? extends V>> entries) {
        return map(new LinkedHashMap<K, V>(), entries);
    }

    public static <K, V> Map<K, V> map(final Map<K, V> seed, final Iterator<? extends Pair<? extends K, ? extends V>> entries) {
        while(entries.hasNext()){
            Pair<? extends K, ? extends V> entry = entries.next();
            seed.put(entry.first(), entry.second());
        }
        return seed;
    }

    public static <T, Key> Map<Key, T> map(final Iterator<? extends T> iterator, final Function1<? super T, ? extends Key> callable) {
        return map(new LinkedHashMap<Key, T>(), iterator, callable);
    }

    public static <T, Key> Map<Key, T> map(final Map<Key, T> seed, final Iterator<? extends T> iterator, final Function1<? super T, ? extends Key> callable) {
        while (iterator.hasNext()) {
            final T next = iterator.next();
            final Key key = call(callable, next);
            seed.put(key, next);
        }
        return seed;
    }

    public static <T, Key> Map<Key, T> map(final Iterable<? extends T> iterable, final Function1<? super T, ? extends Key> callable) {
        return map(iterable.iterator(), callable);
    }

    public static <T, Key> Map<Key, T> map(final Map<Key, T> seed, final Iterable<? extends T> iterable, final Function1<? super T, ? extends Key> callable) {
        return map(seed, iterable.iterator(), callable);
    }

    public static <K, V> Map<K, List<V>> multiMap(final Pair<? extends K, ? extends V> first, final Pair<? extends K, ? extends V> second) {
        return multiMap(sequence(first, second));
    }

    public static <K, V> Map<K, List<V>> multiMap(final Pair<? extends K, ? extends V> first, final Pair<? extends K, ? extends V> second, final Pair<? extends K, ? extends V> third) {
        return multiMap(sequence(first, second, third));
    }

    public static <K, V> Map<K, List<V>> multiMap(final Pair<? extends K, ? extends V> first, final Pair<? extends K, ? extends V> second, final Pair<? extends K, ? extends V> third, final Pair<? extends K, ? extends V> fourth) {
        return multiMap(sequence(first, second, third, fourth));
    }

    public static <K, V> Map<K, List<V>> multiMap(final Pair<? extends K, ? extends V> first, final Pair<? extends K, ? extends V> second, final Pair<? extends K, ? extends V> third, final Pair<? extends K, ? extends V> fourth, final Pair<? extends K, ? extends V> fifth) {
        return multiMap(sequence(first, second, third, fourth, fifth));
    }

    @SafeVarargs
    public static <K, V> Map<K, List<V>> multiMap(final Pair<? extends K, ? extends V>... entries) {
        return multiMap(sequence(entries));
    }

    @SafeVarargs
    public static <K, V> Map<K, List<V>> multiMap(final Map<K, List<V>> seed, final Pair<? extends K, ? extends V>... entries) {
        return multiMap(seed, sequence(entries));
    }

    public static <K, V> Map<K, List<V>> multiMap(final Iterable<? extends Pair<? extends K, ? extends V>> entries) {
        return multiMap(new LinkedHashMap<K, List<V>>(), entries);
    }

    public static <K, V> Map<K, List<V>> multiMap(final Map<K, List<V>> seed, final Iterable<? extends Pair<? extends K, ? extends V>> entries) {
        for (Pair<? extends K, ? extends V> entry : entries) {
            if (!seed.containsKey(entry.first())) {
                seed.put(entry.first(), new ArrayList<V>());
            }
            seed.get(entry.first()).add(entry.second());
        }
        return seed;
    }

    public static <T, Key> Map<Key, List<T>> multiMap(final Iterator<? extends T> iterator, final Function1<? super T, ? extends Key> callable) {
        return multiMap(new LinkedHashMap<Key, List<T>>(), iterator, callable);
    }

    public static <V, K> Map<K, List<V>> multiMap(final Map<K, List<V>> seed, final Iterator<? extends V> iterator, final Function1<? super V, ? extends K> callable) {
        while (iterator.hasNext()) {
            final V value = iterator.next();
            final K key = call(callable, value);
            if (!seed.containsKey(key)) {
                seed.put(key, new ArrayList<V>());
            }
            seed.get(key).add(value);
        }
        return seed;
    }

    public static <V, K> Map<K, List<V>> multiMap(final Iterable<? extends V> iterable, final Function1<? super V, ? extends K> callable) {
        return multiMap(iterable.iterator(), callable);
    }

    public static <V, K> Map<K, List<V>> multiMap(final Map<K, List<V>> seed, final Iterable<? extends V> iterable, final Function1<? super V, ? extends K> callable) {
        return multiMap(seed, iterable.iterator(), callable);
    }

    public static <K, V> Curried2<? super Map<K, List<V>>, ? super Pair<? extends K, ? extends V>, Map<K, List<V>>> asMultiValuedMap() {
        return (map, pair) -> {
            if (!map.containsKey(pair.first())) {
                map.put(pair.first(), new ArrayList<V>());
            }
            map.get(pair.first()).add(pair.second());
            return map;
        };
    }

    public static <K, V> Curried2<? super Map<K, List<V>>, ? super Pair<? extends K, ? extends V>, Map<K, List<V>>> asMultiValuedMap(Class<K> key, Class<V> value) {
        return asMultiValuedMap();
    }

    public static <K, V> Curried2<? super Map<K, V>, ? super Pair<K, V>, Map<K, V>> asMap() {
        return (map, pair) -> {
            map.put(pair.first(), pair.second());
            return map;
        };
    }

    public static <K, V> Curried2<? super Map<K, V>, ? super Pair<K, V>, Map<K, V>> asMap(Class<K> key, Class<V> value) {
        return asMap();
    }

    public static <K, V> Function1<Pair<K, V>, Map.Entry<K, V>> pairToEntry() {
        return pair -> pair;
    }

    public static <K, V> Function1<Pair<K, V>, Map.Entry<K, V>> pairToEntry(final Class<K> keyClass, final Class<V> valueClass) {
        return pairToEntry();
    }

    public static <K, V> Function1<Map.Entry<K, V>, Pair<K, V>> entryToPair() {
        return entry -> pair(entry.getKey(), entry.getValue());
    }

    public static <K, V> Function1<Map.Entry<K, V>, Pair<K, V>> entryToPair(final Class<K> keyClass, final Class<V> valueClass) {
        return entryToPair();
    }

    public static <K, V> Option<V> get(Map<K, V> map, K key) {
        return Option.option(map.get(key));
    }

    public static <K, V> Option<V> find(Map<K, V> map, Predicate<? super K> predicate) {
        return pairs(map).find(where(Callables.<K>first(), predicate)).map(Callables.<V>second());
    }

    public static <K, V> Map<K, V> filterKeys(Map<K, V> map, Predicate<? super K> predicate) {
        return map(pairs(map).filter(where(Callables.<K>first(), predicate)));
    }

    public static <K, V> Map<K, V> filterValues(Map<K, V> map, Predicate<? super V> predicate) {
        return map(pairs(map).filter(where(Callables.<V>second(), predicate)));
    }

    public static <K, V, NewK> Map<NewK, V> mapKeys(Map<K, V> map, Function1<? super K, ? extends NewK> transformer) {
        return map(pairs(map).map(Callables.<K, V, NewK>first(transformer)));
    }

    public static <K, V, NewV> Map<K, NewV> mapValues(Map<K, V> map, Function1<? super V, ? extends NewV> transformer) {
        return map(pairs(map).map(Callables.<K, V, NewV>second(transformer)));
    }

    public static <K, V> Map<K, V> fifoMap(final int maximumElements) {
        return new LinkedHashMap<K, V>(maximumElements) {
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) { return size() > maximumElements; }
        };
    }

    public static <K, V> Map<K, V> lruMap(final int maximumElements) {
        return new LinkedHashMap<K, V>(maximumElements, 1f, true) {
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) { return size() > maximumElements; }
        };
    }

    public static <K, V> Set<Map.Entry<K, V>> entrySet(final Iterable<? extends Pair<K, V>> map) {
        return sequence(map).map(Maps.<K, V>pairToEntry()).toSet();
    }

    public static class functions {
        public static <K, V> Curried2<Map<K, V>, K, V> get() {
            return Map::get;
        }

        public static <K, V> Function1<K, V> getFrom(final Map<K, V> map) {
            return functions.<K,V>get().apply(map);
        }

        public static <K, V> Function1<Map<K, V>, V> valueFor(final K key) {
            return functions.<K,V>get().flip().apply(key);
        }

        public static <K, V> Function1<Map<K, V>, V> valueFor(final K key, final Class<V> vClass) {
            return valueFor(key);
        }
    }
}