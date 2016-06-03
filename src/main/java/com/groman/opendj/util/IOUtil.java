package com.groman.opendj.util;

import java.io.Closeable;

public class IOUtil {

    private IOUtil() {
    }
    
    public static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                throw new RuntimeException("Unable to close resource", e);
            }
        }
    }
}
