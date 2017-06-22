package com.googlecode.totallylazy.functions;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Memory;

import java.util.HashMap;
import java.util.Map;

import static com.googlecode.totallylazy.Closeables.safeClose;

public final class Lazy1<T, R> implements Function1<T, R>, Memory {
    private final Function1<? super T, ? extends R> callable;
    private final Map<T, Either<Exception, R>> state = new HashMap<>();
    private final Object lock = new Object();

    private Lazy1(Function1<? super T, ? extends R> callable) {
        this.callable = callable;
    }

    public static <T, R> Lazy1<T, R> lazy(Function1<? super T, ? extends R> callable) {
        return new Lazy1<>(callable);
    }

    public final R call(T instance) throws Exception{
        synchronized (lock) {
            if (!state.containsKey(instance)) {
                try {
                    state.put(instance, Either.right(callable.call(instance)));
                } catch (Exception e) {
                    state.put(instance, Either.left(e));
                }
            }
            Either<Exception, R> either = state.get(instance);
            if(either.isLeft()) {
                throw either.left();
            }
            return either.right();
        }
    }

    public void forget() {
        close();
    }

    @Override
    public void close() {
        synchronized (lock) {
            for (Either<Exception, R> r : state.values()) {
                if(r != null) safeClose(r.value());
            }
            state.clear();
        }
    }
}
