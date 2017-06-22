package com.googlecode.totallylazy.reflection;

import com.googlecode.totallylazy.functions.Lazy;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.Sequence;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.LineNumberNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.functions.Lazy.lazy;
import static com.googlecode.totallylazy.predicates.Predicates.instanceOf;
import static com.googlecode.totallylazy.predicates.Predicates.not;
import static com.googlecode.totallylazy.Sequences.sequence;

public class StackFrame {
    private final StackTraceElement trace;
    private final Lazy<Class<?>> aClass;
    private final Lazy<ClassNode> classNode;
    private final Lazy<MethodNode> methodNode;
    private final Lazy<Sequence<AbstractInsnNode>> instructions;
    private final Lazy<Method> method;
    private final Lazy<Constructor<?>> constructor;

    public StackFrame(StackTraceElement trace) {
        this.trace = trace;
        Predicate<AbstractInsnNode> lineNumber = instanceOf(LineNumberNode.class, line -> line.line == trace.getLineNumber());
        aClass = lazy(() -> Class.forName(trace.getClassName()));
        classNode = lazy(() -> Asm.classNode(aClass.value()));
        methodNode = lazy(() -> Asm.methods(classNode.value()).
                filter(m -> m.name.equals(trace.getMethodName())).
                filter(m -> Asm.instructions(m).exists(lineNumber)).
                head());
        instructions = lazy(() -> Asm.instructions(methodNode.value()).
                dropWhile(not(lineNumber)).tail().
                takeWhile(not(instanceOf(LineNumberNode.class))).
                memoize());
        method = lazy(() -> sequence(aClass.value().getDeclaredMethods()).
                filter(m -> m.getName().equals(trace.getMethodName())).
                filter(m -> sequence(Type.getArgumentTypes(m)).
                        equals(sequence(Type.getArgumentTypes(methodNode.value().desc)))).
                head());
        constructor = lazy(() -> sequence(aClass.value().getDeclaredConstructors()).
                filter(c -> sequence(Asm.getArgumentTypes(c)).
                        equals(sequence(Type.getArgumentTypes(methodNode.value().desc)))).
                head());
    }

    public StackTraceElement trace() {
        return trace;
    }

    public Class<?> aClass() {
        return aClass.value();
    }

    public ClassNode classNode() {
        return classNode.value();
    }

    public MethodNode methodNode() {
        return methodNode.value();
    }

    public Method method() {
        return method.value();
    }

    public Constructor<?> constructor() {
        return constructor.value();
    }

    public Sequence<AbstractInsnNode> instructions() {
        return instructions.value();
    }

    @Override
    public String toString() {
        return trace().toString();
    }
}
