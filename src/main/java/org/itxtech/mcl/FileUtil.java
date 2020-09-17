package org.itxtech.mcl;

import org.mozilla.javascript.NativeArray;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;

public class FileUtil {
    public static String fileMd5(File file) throws Exception {
        var fis = new FileInputStream(file);
        var buffer = new byte[1024];
        var digest = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                digest.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return new BigInteger(1, digest.digest()).toString(16);
    }

    public static String readSmallFile(File file) throws Exception {
        var fis = new FileInputStream(file);
        var data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return new String(data, StandardCharsets.UTF_8);
    }

    public static boolean check(File baseFile, File checksumFile) throws Exception {
        if (!baseFile.exists() || !checksumFile.exists()) {
            return false;
        }
        var correctMd5 = FileUtil.readSmallFile(checksumFile).trim();
        return fileMd5(baseFile).equals(correctMd5);
    }

    public static URLClassLoader loadJars(NativeArray files) throws MalformedURLException {
        var list = new ArrayList<URL>();
        for (var file : files) {
            if (file instanceof File) {
                list.add(((File) file).toURI().toURL());
            }
        }
        return new URLClassLoader(list.toArray(new URL[0]));
    }

    public static void bootMirai(ClassLoader loader, String entry) throws Exception {
        var method = loader.loadClass(entry).getMethod("main", String[].class);
        method.invoke(null, (Object) new String[]{});
    }
}
