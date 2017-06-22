package com.googlecode.totallylazy;

import com.googlecode.totallylazy.collections.AbstractCollection;
import com.googlecode.totallylazy.collections.Indexed;
import com.googlecode.totallylazy.collections.PersistentCollection;
import com.googlecode.totallylazy.collections.PersistentList;
import com.googlecode.totallylazy.functions.*;
import com.googlecode.totallylazy.predicates.Predicate;

import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import static com.googlecode.totallylazy.functions.Callables.ascending;
import static com.googlecode.totallylazy.functions.Callables.returnArgument;
import static com.googlecode.totallylazy.predicates.Predicates.in;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.not;
import static com.googlecode.totallylazy.Sequences.sequence;


public abstract class Sequence<T> extends AbstractCollection<T> implements Iterable<T>, First<T>, Second<T>, Third<T>, Functor<T>, Segment<T>, PersistentCollection<T>, Applicative<T>, Monad<T>, Foldable<T>, Indexed<T>, Filterable<T> {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Iterable) return Sequences.equalTo(this, (Iterable<?>) obj);
        return obj instanceof Segment && Segment.methods.equalTo(this, (Segment<?>) obj);
    }

    public boolean equals(Iterable<? extends T> other, Predicate<? super Pair<T, T>> predicate) {
        return Sequences.equalTo(this, other, predicate);
    }

    public Sequence<T> eachConcurrently(final Block<? super T> runnable) {
        Sequences.eachConcurrently(this, runnable);
        return this;
    }

    public Sequence<T> eachConcurrently(final Block<? super T> runnable, Executor executor) {
        Sequences.eachConcurrently(this, runnable, executor);
        return this;
    }

    public Sequence<T> eachConcurrently(final Function1<? super T, ?> runnable) {
        Sequences.eachConcurrently(this, runnable);
        return this;
    }

    public Sequence<T> eachConcurrently(final Function1<? super T, ?> runnable, Executor executor) {
        Sequences.eachConcurrently(this, runnable, executor);
        return this;
    }

    public Sequence<T> each(final Block<? super T> runnable) {
        Sequences.each(this, runnable);
        return this;
    }

    public Sequence<T> each(final Function1<? super T, ?> runnable) {
        Sequences.each(this, runnable);
        return this;
    }

    public Sequence<T> tap(final Function1<? super T, ?> callable) { return Sequences.tap(this, callable); }

    public <S> Sequence<S> mapConcurrently(final Function1<? super T, S> callable) {
        return Sequences.mapConcurrently(this, callable);
    }

    public <S> Sequence<S> mapConcurrently(final Function1<? super T, S> callable, final Executor executor) {
        return Sequences.mapConcurrently(this, callable, executor);
    }

    @Override
    public <S> Sequence<S> map(final Function1<? super T, ? extends S> callable) {
        return Sequences.map(this, callable);
    }

    public Pair<Sequence<T>, Sequence<T>> partition(final Predicate<? super T> predicate) {
        return Sequences.partition(this, predicate);
    }

    @Override
    public Sequence<T> filter(final Predicate<? super T> predicate) {
        return Sequences.filter(this, predicate);
    }

    @Override
    public Sequence<T> reject(final Predicate<? super T> predicate) {
        return filter(not(predicate));
    }

    public <S> Sequence<S> flatMap(final Function1<? super T, ? extends Iterable<? extends S>> callable) {
        return Sequences.flatMap(this, callable);
    }

    @SafeVarargs
    public final <S> Sequence<S> collect(Function1<? super T, ? extends Option<S>>... callables) {
        return flatMap(Functions.or(callables));
    }

    public <S> Sequence<S> collect(Predicate<? super T> predicate1, Function1<? super T, ? extends S> function1) {
        return collect(Functions.option(predicate1, function1));
    }

    public <S> Sequence<S> collect(Predicate<? super T> predicate1, Function1<? super T, ? extends S> function1, Predicate<? super T> predicate2, Function1<? super T, ? extends S> callable2) {
        return collect(Functions.option(predicate1, function1), Functions.option(predicate2, callable2));
    }

    public <S> Sequence<S> collect(Predicate<? super T> predicate1, Function1<? super T, ? extends S> function1, Predicate<? super T> predicate2, Function1<? super T, ? extends S> callable2, Predicate<? super T> predicate3, Function1<? super T, ? extends S> callable3) {
        return collect(Functions.option(predicate1, function1), Functions.option(predicate2, callable2), Functions.option(predicate3, callable3));
    }

    public <S> Sequence<S> collect(Predicate<? super T> predicate1, Function1<? super T, ? extends S> function1, Predicate<? super T> predicate2, Function1<? super T, ? extends S> callable2, Predicate<? super T> predicate3, Function1<? super T, ? extends S> callable3, Predicate<? super T> predicate4, Function1<? super T, ? extends S> callable4) {
        return collect(Functions.option(predicate1, function1), Functions.option(predicate2, callable2), Functions.option(predicate3, callable3), Functions.option(predicate4, callable4));
    }

    public <S> Sequence<S> collect(Predicate<? super T> predicate1, Function1<? super T, ? extends S> function1, Predicate<? super T> predicate2, Function1<? super T, ? extends S> callable2, Predicate<? super T> predicate3, Function1<? super T, ? extends S> callable3, Predicate<? super T> predicate4, Function1<? super T, ? extends S> callable4, Predicate<? super T> predicate5, Function1<? super T, ? extends S> callable5) {
        return collect(Functions.option(predicate1, function1), Functions.option(predicate2, callable2), Functions.option(predicate3, callable3), Functions.option(predicate4, callable4), Functions.option(predicate5, callable5));
    }

    public <S> Sequence<S> flatMapConcurrently(final Function1<? super T, ? extends Iterable<? extends S>> callable) {
        return Sequences.flatMapConcurrently(this, callable);
    }

    public <S> Sequence<S> flatMapConcurrently(final Function1<? super T, ? extends Iterable<? extends S>> callable, final Executor executor) {
        return Sequences.flatMapConcurrently(this, callable, executor);
    }

    public <B> Sequence<B> applicate(final Sequence<? extends Function1<? super T, ? extends B>> applicator) {
        return Sequences.applicate(applicator, this);
    }

    public T first() {
        return Sequences.first(this);
    }

    public T last() {
        return Sequences.last(this);
    }

    public Option<T> lastOption() {
        return Sequences.lastOption(this);
    }

    public T second() {
        return Sequences.second(this);
    }

    @Override
    public T third() {
        return Sequences.third(this);
    }

    public T head() {
        return Sequences.head(this);
    }

    public Option<T> headOption() {
        return Sequences.headOption(this);
    }

    public Sequence<T> tail() {
        return Sequences.tail(this);
    }

    public Sequence<T> init() {
        return Sequences.init(this);
    }

    public <S> S fold(final S seed, final Function2<? super S, ? super T, ? extends S> callable) {
        return Sequences.fold(this, seed, callable);
    }

    public <S> S foldLeft(final S seed, final Function2<? super S, ? super T, ? extends S> callable) {
        return Sequences.foldLeft(this, seed, callable);
    }

    public <S> Sequence<S> scanLeft(final S seed, final Function2<? super S, ? super T, ? extends S> callable) {
        return Sequences.scanLeft(this, seed, callable);
    }

    public <S> S foldRight(final S seed, final Function2<? super T, ? super S, ? extends S> callable) {
        return Sequences.foldRight(this, seed, callable);
    }

    public <S> S foldRight(final S seed, final Function1<? super Pair<T, S>, ? extends S> callable) {
        return Sequences.foldRight(this, seed, callable);
    }

    public <S> S reduce(final Function2<? super S, ? super T, ? extends S> callable) {
        return Sequences.reduce(this, callable);
    }

    public <S> S reduceLeft(final Function2<? super S, ? super T, ? extends S> callable) {
        return Sequences.reduceLeft(this, callable);
    }

    public <S> S reduceRight(final Function2<? super T, ? super S, ? extends S> callable) {
        return Sequences.reduceRight(this, callable);
    }

    public <S> S reduceRight(final Function1<? super Pair<T, S>, ? extends S> callable) {
        return Sequences.reduceRight(this, callable);
    }

    public String toString() {
        return Sequences.toString(this);
    }

    public String toString(final String separator) {
        return Sequences.toString(this, separator);
    }

    public String toString(final String start, final String separator, final String end) {
        return Sequences.toString(this, start, separator, end);
    }

    public <A extends Appendable> A appendTo(A appendable) {
        return Sequences.appendTo(this, appendable);
    }

    public <A extends Appendable> A appendTo(A appendable, final String separator) {
        return Sequences.appendTo(this, appendable, separator);
    }

    public <A extends Appendable> A appendTo(A appendable, final String start, final String separator, final String end) {
        return Sequences.appendTo(this, appendable, start, separator, end);
    }

    public Set<T> union(final Iterable<? extends T> other) {
        return Sets.union(toSet(), Sets.set(other));
    }

    public Set<T> intersection(final Iterable<? extends T> other) {
        return Sets.intersection(sequence(toSet(), Sets.set(other)));
    }

    public <S extends Set<T>> S toSet(S set) {
        return Sets.set(set, this);
    }

    public Set<T> toSet() {
        return toSet(new LinkedHashSet<T>());
    }

    public Sequence<T> unique() {
        return unique(returnArgument());
    }

    public <S> Sequence<T> unique(Function1<? super T, ? extends S> callable) {
        return Sequences.unique(this, callable);
    }

    @Override
    public Sequence<T> empty() {
        return Sequences.empty();
    }

    public boolean isEmpty() {
        return Sequences.isEmpty(this);
    }

    public List<T> toList() {
        return Sequences.toList(this);
    }

    public List<T> toSortedList(Comparator<T> comparator) {
        return Sequences.toSortedList(this, comparator);
    }

    public Deque<T> toDeque() {
        return Sequences.toDeque(this);
    }

    @Override
    public Sequence<T> delete(final T t) {
        return Sequences.delete(this, t);
    }

    public Sequence<T> deleteAll(final Iterable<? extends T> iterable) {
        return Sequences.deleteAll(this, iterable);
    }

    public int size() {
        return Sequences.size(this);
    }

    public Number number() {
        return Sequences.number(this);
    }

    public Sequence<T> take(final int count) {
        return Sequences.take(this, count);
    }

    public Sequence<T> takeWhile(final Predicate<? super T> predicate) {
        return Sequences.takeWhile(this, predicate);
    }

    public Sequence<T> drop(final int count) {
        return Sequences.drop(this, count);
    }

    public Sequence<T> dropWhile(final Predicate<? super T> predicate) {
        return Sequences.dropWhile(this, predicate);
    }

    public boolean forAll(final Predicate<? super T> predicate) {
        return Sequences.forAll(this, predicate);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return forAll(in(c));
    }

    @Override
    public boolean contains(Object o) {
        return exists(is(o));
    }

    public boolean exists(final Predicate<? super T> predicate) {
        return Sequences.exists(this, predicate);
    }

    public Option<T> find(final Predicate<? super T> predicate) {
        return Sequences.find(this, predicate);
    }

    public Option<Integer> findIndexOf(final Predicate<? super T> predicate) {
        return zipWithIndex().find(pair -> predicate.matches(pair.second())).map(pair -> pair.first().intValue());
    }

    public <S> Option<S> tryPick(final Function1<? super T, ? extends Option<? extends S>> callable) {
        return Sequences.tryPick(this, callable);
    }

    public <S> S pick(final Function1<? super T, ? extends Option<? extends S>> callable) {
        return Sequences.pick(this, callable);
    }

    public Sequence<T> append(final T t) {
        return Sequences.append(this, t);
    }

    public Sequence<T> join(final Iterable<? extends T> iterable) {
        return Sequences.join(this, iterable);
    }

    @Override
    public <C extends Segment<T>> C joinTo(C rest) {
        return Sequences.joinTo(this, rest);
    }

    public Sequence<T> cons(final T t) {
        return Sequences.cons(t, this);
    }

    public Sequence<T> memoize() {
        return memorise();
    }

    public Sequence<T> memorise() {
        return Sequences.memorise(this);
    }

    public ForwardOnlySequence<T> forwardOnly() {
        return Sequences.forwardOnly(this);
    }

    public <S> Sequence<Pair<T, S>> zip(final Iterable<? extends S> second) {
        return Sequences.zip(this, second);
    }

    @SafeVarargs
    public final Sequence<Sequence<T>> transpose(final Iterable<? extends T>... iterables) {
        return transpose(sequence(iterables));
    }

    public Sequence<Sequence<T>> transpose(final Iterable<? extends Iterable<? extends T>> iterables) {
        return Sequences.transpose(Sequences.cons(this, sequence(iterables).<Iterable<T>>unsafeCast()));
    }

    public <S, Th> Sequence<Triple<T, S, Th>> zip(final Iterable<? extends S> second, final Iterable<? extends Th> third) {
        return Sequences.zip(this, second, third);
    }

    public <S, Th, Fo> Sequence<Quadruple<T, S, Th, Fo>> zip(final Iterable<? extends S> second, final Iterable<? extends Th> third, final Iterable<? extends Fo> fourth) {
        return Sequences.zip(this, second, third, fourth);
    }

    public <S, Th, Fo, Fi> Sequence<Quintuple<T, S, Th, Fo, Fi>> zip(final Iterable<? extends S> second, final Iterable<? extends Th> third, final Iterable<? extends Fo> fourth, final Iterable<? extends Fi> fifth) {
        return Sequences.zip(this, second, third, fourth, fifth);
    }


    public Sequence<Pair<Number, T>> zipWithIndex() {
        return Sequences.zipWithIndex(this);
    }

    public <R extends Comparable<? super R>> Sequence<T> sortBy(final Function1<? super T, ? extends R> callable) {
        return sortBy(ascending(callable));
    }

    public Sequence<T> sort(final Comparator<? super T> comparator) {
        return sortBy(comparator);
    }

    public Sequence<T> sortBy(final Comparator<? super T> comparator) {
        return Sequences.sortBy(this, comparator);
    }

    public <S> Sequence<S> safeCast(final Class<? extends S> aClass) {
        return Sequences.safeCast(this, aClass);
    }

    public <S> Sequence<S> unsafeCast() {
        return Sequences.unsafeCast(this);
    }

    public Sequence<T> realise() {
        return Sequences.realise(this);
    }

    public Sequence<T> reverse() {
        return Sequences.reverse(this);
    }

    public Sequence<T> cycle() {
        return Sequences.cycle(this);
    }

    public <K> Map<K, List<T>> toMap(final Function1<? super T, ? extends K> callable) {
        return Maps.multiMap(this, callable);
    }

    public <K> Sequence<Group<K, T>> groupBy(final Function1<? super T, ? extends K> callable) {
        return Sequences.groupBy(this, callable);
    }

    public Sequence<Sequence<T>> recursive(final Function1<Sequence<T>, Pair<Sequence<T>, Sequence<T>>> callable) {
        return Sequences.recursive(this, callable);
    }

    public Pair<Sequence<T>, Sequence<T>> splitAt(final Number index) {
        return Sequences.splitAt(this, index);
    }

    public Pair<Sequence<T>, Sequence<T>> splitWhen(final Predicate<? super T> predicate) {
        return Sequences.splitWhen(this, predicate);
    }

    public Pair<Sequence<T>, Sequence<T>> splitOn(final T instance) {
        return Sequences.splitOn(this, instance);
    }

    public Pair<Sequence<T>, Sequence<T>> span(final Predicate<? super T> predicate) {
        return Sequences.span(this, predicate);
    }

    public Pair<Sequence<T>, Sequence<T>> breakOn(final Predicate<? super T> predicate) {
        return Sequences.breakOn(this, predicate);
    }

    public Sequence<T> shuffle() {
        return Sequences.shuffle(this);
    }

    public Sequence<T> interruptable() {
        return Sequences.interruptable(this);
    }

    public PersistentList<T> toPersistentList() {
        return PersistentList.constructors.list(this);
    }

    public Sequence<Pair<T, T>> cartesianProduct() {
        return Sequences.cartesianProduct(this);
    }

    public <S> Sequence<Pair<T, S>> cartesianProduct(final Iterable<? extends S> other) {
        return Sequences.cartesianProduct(this, other);
    }

    public T get(int index) {
        return drop(index).head();
    }

    public Sequence<Sequence<T>> windowed(int size) {
        return Sequences.windowed(this, size);
    }

    public Sequence<Sequence<T>> windowed(int step, int size) {
        return Sequences.windowed(this, step, size);
    }

    public Sequence<T> intersperse(T separator) {
        return Sequences.intersperse(this, separator);
    }

    public Option<Sequence<T>> flatOption() {
        return Sequences.flatOption(this);
    }

    @Override
    public int indexOf(Object t) {
        return Sequences.indexOf(this, t);
    }

    public Sequence<Sequence<T>> grouped(int size) {
        return recursive(Sequences.<T>splitAt(size));
    }

    public static class functions {
        public static <T> Unary<Sequence<T>> tail() {
            return Segment.functions.<T, Sequence<T>>tail();
        }

        public static <T> Unary<Sequence<T>> tail(Class<T> aClass) {
            return tail();
        }

        public static <T> Curried2<Iterable<? extends T>, Iterable<? extends T>, Sequence<T>> join() {
            return (a, b) -> sequence(Iterators.functions.<T>join().call(a, b));
        }
    }
}
