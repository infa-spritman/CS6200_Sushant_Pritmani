package com.googlecode.totallylazy.proxy;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.reflection.Asm;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.reflection.Fields.fields;
import static com.googlecode.totallylazy.reflection.Fields.name;
import static com.googlecode.totallylazy.LazyException.lazyException;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.totallylazy.proxy.Generics.getGenericSuperclassType;
import static com.googlecode.totallylazy.proxy.Proxy.proxy;
import static java.lang.Class.forName;

public abstract class FieldOn<T, S> implements Function1<T, S>, InvocationHandler {
    private final FieldInvocation<T, S> invocation;
    protected final T instance;


    public FieldOn() {
        Class<T> aClass = getGenericSuperclassType(this.getClass(), 0);
        instance = proxy(aClass, this);
        invocation = new FieldInvocation<T, S>(readFieldFromByteCode());
    }

    private Field readFieldFromByteCode() {
        try {
            ClassNode classNode = Asm.classNode(getClass());
            MethodNode constructor = classNode.methods.get(0);
            FieldInsnNode fieldInsnNode = Asm.instructions(constructor).safeCast(FieldInsnNode.class).last();
            Class<?> aClass = forName(fieldInsnNode.owner.replace('/', '.'));
            return fields(aClass).
                    find(where(name, is(fieldInsnNode.name))).
                    get();
        } catch (Exception e) {
            throw lazyException(e);
        }
    }

    protected void get(S s) {
    }

    @Override
    public S call(T t) throws Exception {
        return invocation.call(t);
    }

    public FieldInvocation<T, S> invocation() {
        return invocation;
    }

    public Field field() {
        return invocation.field();
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        throw new UnsupportedOperationException("Please use CallOn for method invocation");
    }

}
