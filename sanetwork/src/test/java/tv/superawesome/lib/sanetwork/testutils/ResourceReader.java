package tv.superawesome.lib.sanetwork.testutils;

import org.mockito.internal.util.io.IOUtil;

import java.io.InputStream;

import okio.Buffer;

/**
 * Created by gabriel.coman on 30/04/2018.
 */

public class ResourceReader {

    public static Buffer readResource (String name) throws Exception {
        InputStream str = ClassLoader.getSystemClassLoader().getResourceAsStream(name);
        Buffer buffer = new Buffer();
        buffer.readFrom(str);
        return buffer;
    }
}
