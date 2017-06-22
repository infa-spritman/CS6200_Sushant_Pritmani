package com.googlecode.totallylazy.comparators;

import com.googlecode.totallylazy.functions.Monoid;
import com.googlecode.totallylazy.functions.CurriedMonoid;

public interface Minimum<T> extends Monoid<T> {
    class methods {
        public static <T extends Comparable<? super T>> T minimum(T a, T b) {
            return NullComparator.compare(a, b, NullComparator.Direction.Up) > 0 ? b : a;
        }
    }

    class constructors {
        public static <T extends Comparable<? super T>> Function<T> minimum(final T identity) {
            return new Function<T>(identity);
        }
    }

    class Function<T extends Comparable<? super T>> implements Minimum<T>,CurriedMonoid<T> {
        private final T identity;

        private Function(T identity) {this.identity = identity;}

        @Override
        public T call(T t, T t2) throws Exception {
            return methods.minimum(t, t2);
        }

        @Override
        public T identity() {
            return identity;
        }
    }
}