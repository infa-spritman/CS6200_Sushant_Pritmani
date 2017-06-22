package com.googlecode.totallylazy.reflection;

import com.googlecode.totallylazy.Bytes;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Unchecked;
import com.googlecode.totallylazy.iterators.ReadOnlyIterator;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.LocalVariableNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

import static com.googlecode.totallylazy.Sequences.sequence;

public class Asm {
    private static final Map<Class<?>, WeakReference<ClassNode>> cache = Collections.synchronizedMap(new WeakHashMap<>());

    public static ClassNode classNode(final Class<?> aClass) {
        ClassNode classNode = cache.computeIfAbsent(aClass, c -> new WeakReference<>(create(c))).get();
        if(classNode == null) {
            classNode = create(aClass);
            cache.put(aClass, new WeakReference<>(classNode));
        }
        return classNode;
    }

    private static ClassNode create(Class<?> c) {
        return classNode(Bytes.bytes(c));
    }

    public static ClassNode classNode(final byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);
        return classNode;
    }

    public static Sequence<MethodNode> methods(ClassNode classNode) {
        return sequence(Unchecked.<List<MethodNode>>cast(classNode.methods));
    }


    public static Sequence<AbstractInsnNode> instructions(MethodNode method) {
        return instructions(method.instructions);
    }

    public static Sequence<AbstractInsnNode> instructions(final InsnList instructions) {
        return new Sequence<AbstractInsnNode>() {
            @Override
            public Iterator<AbstractInsnNode> iterator() {
                return new InsnIterator(instructions);
            }
        };
    }

    public static Sequence<LocalVariableNode> localVariables(MethodNode methodNode) {
        return sequence(methodNode.localVariables);
    }

    public static Sequence<FieldNode> fields(ClassNode classNode) {
        return sequence(classNode.fields);
    }

    public static class InsnIterator extends ReadOnlyIterator<AbstractInsnNode> {
        private final InsnList list;
        private int index = 0;

        public InsnIterator(final InsnList list) {
            this.list = list;
        }

        public final boolean hasNext() {
            return index < list.size();
        }

        public final AbstractInsnNode next() {
            if (hasNext()) {
                return list.get(index++);
            }
            throw new NoSuchElementException();
        }
    }

    public static Type[] getArgumentTypes(Constructor<?> constructor) {
        Class[] var1 = constructor.getParameterTypes();
        Type[] var2 = new Type[var1.length];

        for(int var3 = var1.length - 1; var3 >= 0; --var3) {
            var2[var3] = Type.getType(var1[var3]);
        }

        return var2;
    }

    public static int store(Class<?> aClass) {
        return Type.getType(aClass).getOpcode(Opcodes.ISTORE);
    }

    public static int load(Class<?> aClass) {
        return Type.getType(aClass).getOpcode(Opcodes.ILOAD);
    }

    public static int returns(Class<?> aClass) {
        return Type.getType(aClass).getOpcode(Opcodes.IRETURN);
    }

}
