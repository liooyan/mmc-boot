package cn.lioyan.autoconfigure.file.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * {@link IOUtils}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class IOUtils {
    public static void copyBytes(InputStream in, OutputStream out, int buffSize)
            throws IOException {
        PrintStream ps = out instanceof PrintStream ? (PrintStream) out : null;
        byte buf[] = new byte[buffSize];
        int bytesRead = in.read(buf);
        try {
            while (bytesRead >= 0) {
                out.write(buf, 0, bytesRead);
                if ((ps != null) && ps.checkError()) {
                    throw new IOException("Unable to write to output stream.");
                }
                bytesRead = in.read(buf);
            }
        } finally {
            in.close();
        }
    }
}
