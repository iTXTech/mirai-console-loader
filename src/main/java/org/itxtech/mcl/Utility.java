package org.itxtech.mcl;

import org.mozilla.javascript.NativeArray;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

/*
 *
 * Mirai Console Loader
 *
 * Copyright (C) 2020 iTX Technologies
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author PeratX
 * @website https://github.com/iTXTech/mirai-console-loader
 *
 */
public class Utility {
    public static String fileSha1(File file) throws Exception {
        var fis = new FileInputStream(file);
        var buffer = new byte[1024];
        var digest = MessageDigest.getInstance("sHA");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                digest.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        byte[] bytes = digest.digest();
        BigInteger b = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", b);
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
        var checksum = Utility.readSmallFile(checksumFile).trim().replace(" ", "").toLowerCase();
        return fileSha1(baseFile).equals(checksum);
    }

    public static void bootMirai(NativeArray files, String entry, String args) throws Exception {
        var list = new ArrayList<URL>();
        for (var file : files) {
            if (file instanceof File) {
                list.add(((File) file).toURI().toURL());
            }
        }
        var loader = new URLClassLoader(list.toArray(new URL[0]));
        var method = loader.loadClass(entry).getMethod("main", String[].class);
        method.invoke(null, (Object) (args.trim().equals("") ? new String[0] : args.split(" ")));
    }

    public static String humanReadableFileSize(int bytes) {
        var absB = bytes == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        var value = absB;
        var ci = new StringCharacterIterator("KMGTPE");
        for (var i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Integer.signum(bytes);
        return String.format("%.2f %cB", value / 1024.0, ci.current());
    }

    public static String join(String d, List<String> t) {
        return String.join(d, t);
    }
}
