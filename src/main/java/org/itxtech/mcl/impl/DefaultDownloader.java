package org.itxtech.mcl.impl;

import org.itxtech.mcl.Loader;
import org.itxtech.mcl.component.DownloadObserver;
import org.itxtech.mcl.component.Downloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Proxy;
import java.net.URL;

/*
 *
 * Mirai Console Loader
 *
 * Copyright (C) 2020-2022 iTX Technologies
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
public class DefaultDownloader implements Downloader {
    private final Loader loader;

    public DefaultDownloader(Loader loader) {
        this.loader = loader;
    }

    @Override
    public void download(String url, File file, DownloadObserver observer) {
        try {
            var proxy = loader.getProxy();
            var connection = proxy == null ? new URL(url).openConnection() : new URL(url).openConnection(new Proxy(Proxy.Type.HTTP, proxy));
            var totalLen = connection.getContentLength();
            var is = new BufferedInputStream(connection.getInputStream());
            var os = new FileOutputStream(file);
            var len = 0;
            var buff = new byte[1024];
            var current = 0;
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                current += len;
                if (observer != null) {
                    observer.updateProgress(totalLen, current);
                }
            }
            os.close();
            is.close();
        } catch (Throwable e) {
            loader.logger.logException(e);
        }
    }
}
