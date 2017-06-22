package com.googlecode.totallylazy.functions;

import com.googlecode.totallylazy.*;

import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Pair.pair;

public interface Function1<A, B> extends Functor<B> {
     B call(A a) throws Exception;

     default B apply(A a) {
         return Functions.call(this, a);
     }

     default Function0<B> deferApply(A a) {
         return Callables.deferApply(this, a);
     }

     default Function0<B> callConcurrently(A a) {
         return Callers.callConcurrently(deferApply(a));
     }

     default Function1<A, B> lazy() {
         return Lazy1.lazy(this);
     }

     default Function1<A, B> sleep(int millis) {
         return Sleepy1.sleepy(this, millis);
     }

     default Function1<A, Option<B>> optional() {
         return Exceptions.optional(this);
     }

     default Function1<A, Either<Exception, B>> either() {
         return Exceptions.either(this);
     }

     default Function1<A, Either<Exception, B>> orException() {
         return either();
     }

     default Function1<A, B> orElse(B result) {
         return Exceptions.orElse(this, result);
     }

     @Override
     default <C> Function1<A, C> map(Function1<? super B, ? extends C> callable) {
         return Callables.compose(this, callable);
     }

     default <C> Function1<A, C> then(Function1<? super B, ? extends C> callable) {
         return map(callable);
     }

     default <C> Function1<A, C> then(Callable<? extends C> callable) {
         return Callables.compose(this, callable);
     }

     default Function1<A, B> interruptable() {
         return Functions.interruptable(this);
     }

     default Function1<A, Function0<B>> deferExecution() {
         return Callables.deferReturn(this);
     }

     default Function1<A, Pair<A, B>> capturing() {
         return original -> pair(original, Function1.this.apply(original));
     }

     default Function1<A, B> time() {
         return Time1.time1(this);
     }

     default Function1<A, B> time(Function1<? super Number, ?> reporter) {
         return Time1.time1(this, reporter);
     }

     default Option<B> $(Option<? extends A> applicative) {
         return applicative.applicate(Option.some(this));
     }

     default <L> Either<L, B> $(Either<L, ? extends A> applicative) {
         return applicative.applicate(Either.<L, Function1<A, B>>right(this));
     }

     default Sequence<B> $(Sequence<? extends A> applicative) {
         return applicative.applicate(Sequences.one(this));
     }
}
