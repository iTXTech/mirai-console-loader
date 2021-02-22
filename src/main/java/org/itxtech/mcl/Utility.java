package org.itxtech.mcl;

import org.itxtech.mcl.component.Config;
import org.mozilla.javascript.NativeArray;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.jar.JarFile;

/*
 *
 * Mirai Console Loader
 *
 * Copyright (C) 2020-2021 iTX Technologies
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
        var digest = MessageDigest.getInstance("SHA");
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

    public static boolean check(File baseFile, File checksumFile) throws Exception {
        if (!baseFile.exists() || !checksumFile.exists()) {
            return false;
        }
        var checksum = Files.readString(checksumFile.toPath()).trim().replace(" ", "").toLowerCase();
        return fileSha1(baseFile).equals(checksum);
    }

    public static boolean checkLocalFile(Config.Package pkg) throws Exception {
        var dir = new File(pkg.type);
        dir.mkdirs();
        return Utility.check(pkg.getJarFile(), new File(dir, pkg.getBasename() + ".sha1"));
    }

    public static void bootMirai(NativeArray files, String entry, String args) throws Exception {
        for (var file : files) {
            if (file instanceof File) {
                Agent.appendJarFile(new JarFile((File) file));
            }
        }
        var method = Utility.class.getClassLoader().loadClass(entry).getMethod("main", String[].class);
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
