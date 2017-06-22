package com.googlecode.totallylazy;

import com.googlecode.totallylazy.functions.Function0;

import java.io.*;

import static com.googlecode.totallylazy.functions.Block.block;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.LazyException.lazyException;
import static com.googlecode.totallylazy.predicates.Predicates.notNullValue;
import static com.googlecode.totallylazy.Sequences.repeat;

public class Streams {
    public static void copyAndClose(final InputStream input, final OutputStream out) {
        using(input, inputStream ->
                using(out, block(outputStream ->
                        copy(input, out))));
    }

    public static void copy(InputStream input, OutputStream out) throws IOException {
        copy(input, out, 4096);
    }

    public static void copy(InputStream input, OutputStream out, int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = input.read(buffer)) > 0) {
            out.write(buffer, 0, read);
        }
    }

    public static InputStream emptyInputStream() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
    }

    public static OutputStream nullOutputStream() {
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        };
    }

    public static PrintStream nullPrintStream() {
        return new PrintStream(nullOutputStream());
    }

    public static OutputStream streams(final OutputStream... streams) {
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                for (OutputStream stream : streams) {
                    stream.write(b);
                }
            }
        };
    }

    public static InputStreamReader inputStreamReader(InputStream stream) {
        return new InputStreamReader(stream, Strings.UTF8);
    }

    public static Sequence<String> lines(File file) {
        try {
            return lines(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw lazyException(e);
        }
    }

    public static Sequence<String> lines(InputStream stream) {
        return lines(inputStreamReader(stream));
    }

    public static Sequence<String> lines(Reader reader) {
        return repeat(readLine(new BufferedReader(reader))).takeWhile(notNullValue(String.class));
    }

    public static Sequence<String> lines(String lines) {
        return lines(new StringReader(lines));
    }

    public static Function0<String> readLine(final BufferedReader reader) {
        return () -> {
            String result = reader.readLine();
            if (result == null) {
                reader.close();
            }
            return result;
        };
    }
}
