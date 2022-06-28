package com.fico.cbs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

public class ResourceUtils {
    public ResourceUtils() {
    }

    private static File getRootDir() {
        URL url = ResourceUtils.class.getResource("/");

        try {
            File dir = new File(url.toURI());
            return dir;
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static String getResourceAsPath(String name) {
        File root = getRootDir();
        return find(root, name);
    }

    public static FileInputStream getResourceAsStream(String name) throws FileNotFoundException {
        String path = getResourceAsPath(name);
        return path != null ? new FileInputStream(path) : null;
    }

    private static String find(File f, String name) {
        if (f.isFile()) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f.getPath();
            }
        } else {
            if (!f.isDirectory()) {
                return null;
            }

            File[] fs = f.listFiles();

            for(int i = 0; i < fs.length; ++i) {
                String path = find(fs[i], name);
                if (path != null) {
                    return path;
                }
            }
        }

        return null;
    }
}
